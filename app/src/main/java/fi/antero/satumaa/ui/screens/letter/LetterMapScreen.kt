package fi.antero.satumaa.ui.screens.letter

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LetterMapScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    // Käyttäjän sijainti osmdroid-muodossa
    var userLocation by remember { mutableStateOf<GeoPoint?>(null) }
    var mapCentered by remember { mutableStateOf(false) }
    // Tallennetaan sijaintiluvan tila
    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        )
    }

    // Animaatiologiikka, jossa arvo liikkuu 0.0 - 1.0 välillä
    val travelProgress = remember { Animatable(0f) }
    val travelDuration = remember { Random.nextInt(10000, 20000) } // Randomisoitu kirjeen kulkuaika halutulla välillä

    // Sijaintiluvan käsittely
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted -> hasLocationPermission = isGranted }

    // Haetaan sijainti ja muunnetaan Location -> GeoPoint
    LaunchedEffect(hasLocationPermission) {
        if (hasLocationPermission) {
            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener { location: Location? ->
                    location?.let {
                        userLocation = GeoPoint(it.latitude, it.longitude)
                    }
                }
        } else {
            launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    // Joulupukin Pajakylä, Korvatunturi
    val santaPoint = GeoPoint(66.5435, 25.8481)

    // Kirjeen kulkuanimaatio, aloitetaan kunnes sijainti on löytynyt
    LaunchedEffect(userLocation) {
        if (userLocation != null) {
            // Lisätty viive, ennen kuin kirje lähtee matkaan
            kotlinx.coroutines.delay(1500)

            travelProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = travelDuration, easing = LinearEasing)
            )
        }
    }

    // Etäisyyden laskenta, tuloksena kilometrit
    val distanceKm = remember(userLocation) {
        userLocation?.let { user ->
            val results = FloatArray(1)
            Location.distanceBetween(user.latitude, user.longitude, santaPoint.latitude, santaPoint.longitude, results)
            results[0] / 1000
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
            if (userLocation != null) {
                // Kartan asetuksien määritys (OpenStreetMap)
                AndroidView(
                    factory = {
                        mapView.apply {
                            setTileSource(TileSourceFactory.MAPNIK)
                            this.isVerticalMapRepetitionEnabled = false
                            this.isHorizontalMapRepetitionEnabled = false
                            Configuration.getInstance().userAgentValue = context.packageName
                            setMultiTouchControls(true)
                            minZoomLevel = 3.0
                        }
                    },
                    modifier = Modifier.fillMaxSize(),
                    update = { view ->
                        val user = userLocation ?: return@AndroidView

                        // Kirjeen liikkuminen kartalla viivaa pitkin
                        val projection = view.projection
                        val startPoint = android.graphics.Point()
                        val endPoint = android.graphics.Point()

                        // Lasketaan reitti pikseleinä koordinaattien sijaan, jotta kirje pysyy viivan päällä
                        projection.toPixels(user, startPoint)
                        projection.toPixels(santaPoint, endPoint)

                        val currentX = startPoint.x + (endPoint.x - startPoint.x) * travelProgress.value
                        val currentY = startPoint.y + (endPoint.y - startPoint.y) * travelProgress.value

                        // Muutetaan pikselit takaisin GeoPointiin, jotta marker saa sijainnin
                        val currentPos = projection.fromPixels(currentX.toInt(), currentY.toInt()) as GeoPoint

                        view.overlays.clear()

                        // Viiva kartalla käyttyjän ja joulupukin välillä
                        val line = Polyline().apply {
                            setPoints(listOf(user, santaPoint))
                            outlinePaint.color = android.graphics.Color.RED
                            outlinePaint.strokeWidth = 8f
                            isGeodesic = false
                        }
                        view.overlays.add(line)

                        // Käyttäjän ja joulupukin markerit
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

                        // Kirjeen markeri, joka kulkee kartalla
                        val deliveryMarker = Marker(view).apply {
                            position = currentPos
                            title = "Kirje on matkalla..."
                            icon = ContextCompat.getDrawable(context, android.R.drawable.ic_menu_send)
                            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                            val bearing = user.bearingTo(santaPoint).toFloat()
                            rotation = -bearing + 90f
                        }
                        view.overlays.add(deliveryMarker)

                        // Kartta seuraa kirjeen liikkumista
                        if (travelProgress.value < 1f) {
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

                // Kirjeen kulkutila-laatikko
                Card(
                    modifier = Modifier.align(Alignment.BottomCenter).padding(24.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        if (travelProgress.value < 1f) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Kirje on matkalla...", style = MaterialTheme.typography.labelLarge)

                                // Prosenttilaskuri matkasta, joka muuntaa arvot 0.0-1.0 -> 0-100%
                                Text(
                                    text = "${(travelProgress.value * 100).toInt()} %",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            LinearProgressIndicator(
                                progress = { travelProgress.value },
                                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                            )
                        } else {
                            Text("Joulupukki sai kirjeesi!", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                            Text("Kirje kulki: ${"%.1f".format(distanceKm)} km matkan.", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            } else {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator()
                        Spacer(Modifier.height(16.dp))
                        Text("Etsitään sijaintiasi...", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}