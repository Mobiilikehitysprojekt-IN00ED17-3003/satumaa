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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import fi.antero.satumaa.util.TravelTimeCalculator
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import kotlin.random.Random
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LetterMapScreen(
    letterId: String, // UUSI PARAMETRI: Tarvitaan matka-ajan laskentaan
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

    // UUSI: Lasketaan kesto samalla kaavalla kuin muuallakin, jotta kaikki on synkassa
    val travelDuration = remember(letterId) {
        TravelTimeCalculator.getTravelDuration(letterId).toFloat()
    }

    // Tässä on avain: Lasketaan progress kellonajan perusteella!
    // Emme käytä Animatablea, vaan muuttuvaa tilaa joka päivittyy
    var progress by remember { mutableStateOf(0f) }

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

    // Kirjeen kulkuanimaatio: Päivitetään progress kellon mukaan
    LaunchedEffect(userLocation) {
        if (userLocation != null) {
            val startTime = System.currentTimeMillis()
            while (progress < 1f) {
                val now = System.currentTimeMillis()
                val elapsed = now - startTime
                // Lisätään pieni "alkuviive" (esim. 1s) jotta ehditään nähdä lähtö
                val effectiveElapsed = (elapsed - 1000).coerceAtLeast(0)

                progress = (effectiveElapsed / travelDuration).coerceIn(0f, 1f)

                delay(16) // ~60fps
            }
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

                        val currentX = startPoint.x + (endPoint.x - startPoint.x) * progress
                        val currentY = startPoint.y + (endPoint.y - startPoint.y) * progress

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

                // Kirjeen kulkutila-laatikko
                Card(
                    modifier = Modifier.align(Alignment.BottomCenter).padding(24.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFFF3E6).copy(alpha = 0.95f)
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        if (progress < 1f) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Kirje on matkalla...", style = MaterialTheme.typography.labelLarge)

                                // Prosenttilaskuri matkasta, joka muuntaa arvot 0.0-1.0 -> 0-100%
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

                            // UUSI: Nappi, jolla palataan takaisin flow-näkymään odottamaan vastausta
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
