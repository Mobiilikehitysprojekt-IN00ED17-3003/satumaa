package fi.antero.satumaa.ui.screens.letter

import android.location.Location
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import fi.antero.satumaa.util.TravelTimeCalculator
import fi.antero.satumaa.viewmodel.letter.LetterViewModel
import kotlinx.coroutines.delay
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LetterMapScreen(
    letterId: String,
    onBack: () -> Unit,
    vm: LetterViewModel
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val state by vm.uiState.collectAsState()

    LaunchedEffect(letterId) {
        vm.setActiveLetter(letterId)
    }

    val santaPoint = GeoPoint(66.5435, 25.8481)

    var mapCentered by remember { mutableStateOf(false) }
    var progress by remember { mutableStateOf(0f) }

    val createdAtMs = state.currentLetterCreatedAtMs ?: System.currentTimeMillis()
    val endTime = remember(letterId, createdAtMs) {
        TravelTimeCalculator.getDeliveryTime(letterId, createdAtMs)
    }
    val durationMs = remember(createdAtMs, endTime) {
        (endTime - createdAtMs).coerceAtLeast(1000L)
    }

    LaunchedEffect(letterId, createdAtMs, endTime) {
        while (true) {
            val now = System.currentTimeMillis()
            val elapsed = (now - createdAtMs).coerceAtLeast(0L)
            progress = (elapsed.toFloat() / durationMs.toFloat()).coerceIn(0f, 1f)
            delay(16)
        }
    }

    val userGeoPoint = state.userLocation?.let { GeoPoint(it.latitude, it.longitude) }

    val distanceKm = remember(userGeoPoint) {
        userGeoPoint?.let { user ->
            val results = FloatArray(1)
            Location.distanceBetween(
                user.latitude,
                user.longitude,
                santaPoint.latitude,
                santaPoint.longitude,
                results
            )
            results[0] / 1000f
        } ?: 0f
    }

    val mapView = remember { MapView(context) }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kirjeen matka Joulupukille") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Takaisin")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {

            if (userGeoPoint == null) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                    Spacer(Modifier.height(12.dp))
                    Text("Etsitään sijaintiasi...", style = MaterialTheme.typography.bodyMedium)
                }
                return@Box
            }

            AndroidView(
                factory = {
                    mapView.apply {
                        setTileSource(TileSourceFactory.MAPNIK)
                        isVerticalMapRepetitionEnabled = false
                        isHorizontalMapRepetitionEnabled = false
                        Configuration.getInstance().userAgentValue = context.packageName
                        setMultiTouchControls(true)
                        minZoomLevel = 3.0
                    }
                },
                modifier = Modifier.fillMaxSize(),
                update = { view ->
                    val user = userGeoPoint

                    val projection = view.projection
                    val startPoint = android.graphics.Point()
                    val endPoint = android.graphics.Point()

                    projection.toPixels(user, startPoint)
                    projection.toPixels(santaPoint, endPoint)

                    val currentX = startPoint.x + (endPoint.x - startPoint.x) * progress
                    val currentY = startPoint.y + (endPoint.y - startPoint.y) * progress

                    val currentPos = projection.fromPixels(currentX.toInt(), currentY.toInt()) as GeoPoint

                    view.overlays.clear()

                    val line = Polyline().apply {
                        setPoints(listOf(user, santaPoint))
                        outlinePaint.color = android.graphics.Color.RED
                        outlinePaint.strokeWidth = 8f
                        isGeodesic = false
                    }
                    view.overlays.add(line)

                    view.overlays.add(Marker(view).apply {
                        position = user
                        title = "Sinä"
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    })
                    view.overlays.add(Marker(view).apply {
                        position = santaPoint
                        title = "Joulupukki"
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    })

                    val deliveryMarker = Marker(view).apply {
                        position = currentPos
                        title = "Kirje on matkalla..."
                        icon = androidx.core.content.ContextCompat.getDrawable(
                            context,
                            android.R.drawable.ic_menu_send
                        )
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                        val bearing = user.bearingTo(santaPoint).toFloat()
                        rotation = -bearing + 90f
                    }
                    view.overlays.add(deliveryMarker)

                    if (progress < 1f) {
                        view.controller.setCenter(currentPos)
                    }

                    if (!mapCentered) {
                        view.controller.setZoom(7.0)
                        view.controller.setCenter(currentPos)
                        mapCentered = true
                    }

                    view.invalidate()
                }
            )

            Card(
                modifier = Modifier.align(Alignment.BottomCenter).padding(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFFF3E6).copy(alpha = 0.95f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (progress < 1f) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Kirje on matkalla...", style = MaterialTheme.typography.labelLarge)
                            Text(
                                text = "${(progress * 100).toInt()} %",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        )
                    } else {
                        Text(
                            "Joulupukki sai kirjeesi!",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "Kirje kulki: ${"%.1f".format(distanceKm)} km matkan.",
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Spacer(Modifier.height(16.dp))

                        Button(
                            onClick = onBack,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Palaa odottamaan vastausta")
                        }
                    }
                }
            }
        }
    }
}
