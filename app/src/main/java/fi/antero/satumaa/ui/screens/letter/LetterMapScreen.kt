package fi.antero.satumaa.ui.screens.letter

import android.location.Location
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import fi.antero.satumaa.R
import fi.antero.satumaa.ui.components.AppTopBar
import fi.antero.satumaa.ui.components.letter.map.DeliveryStatusCard
import fi.antero.satumaa.ui.components.letter.map.LetterMapOverlay
import fi.antero.satumaa.ui.components.letter.map.MapLoadingView
import fi.antero.satumaa.util.TravelTimeCalculator
import fi.antero.satumaa.viewmodel.letter.LetterViewModel
import kotlinx.coroutines.delay
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView

/**
 * LetterMapScreen visualisoi kirjeen matkan kartalla.
 *
 * Logiikka:
 * - Laskee etäisyyden ja animaation edistymisen (progress) paikallisesti.
 * - Käyttää alikomponentteja (LetterMapOverlay, DeliveryStatusCard) piirtämiseen.
 */
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

    // Asetetaan aktiivinen kirje ViewModeliin, kun sivu aukeaa
    LaunchedEffect(letterId) {
        vm.setActiveLetter(letterId)
    }

    val santaPoint = GeoPoint(66.5435, 25.8481) // Korvatunturin koordinaatit (suuntaa-antava)

    // Tilamuuttujat animaatiolle ja kartalle
    var mapCentered by remember { mutableStateOf(false) }
    var progress by remember { mutableFloatStateOf(0f) }

    // Haetaan kirjeen luontiaika ja lasketaan arvioitu saapumisaika (TravelTimeCalculatorilla)
    val createdAtMs = state.currentLetterCreatedAtMs ?: System.currentTimeMillis()
    val endTime = remember(letterId, createdAtMs) {
        TravelTimeCalculator.getDeliveryTime(letterId, createdAtMs)
    }
    // Varmistetaan, että matka kestää ainakin hetken (1s), jotta ei tule nollalla jakoa
    val durationMs = remember(createdAtMs, endTime) {
        (endTime - createdAtMs).coerceAtLeast(1000L)
    }

    // Animaatiosilmukka: Päivittää progress-muuttujaa (0.0 -> 1.0) reaaliajassa
    LaunchedEffect(letterId, createdAtMs, endTime) {
        while (true) {
            val now = System.currentTimeMillis()
            val elapsed = (now - createdAtMs).coerceAtLeast(0L)
            progress = (elapsed.toFloat() / durationMs.toFloat()).coerceIn(0f, 1f)
            delay(16) // Päivitys n. 60 kertaa sekunnissa (60fps)
        }
    }

    val userGeoPoint = state.userLocation?.let { GeoPoint(it.latitude, it.longitude) }

    // Lasketaan etäisyys (km) paikallisesti käyttäjän ja pukin välillä
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

    // MapView:n elinkaaren hallinta (estää muistivuodot ja mustat ruudut, kun sovellus menee taustalle)
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
            AppTopBar(
                overrideTitle = stringResource(R.string.letter_map_title),
                showBack = true,
                onBack = onBack
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {

            if (userGeoPoint == null) {
                // 1. Jos sijaintia ei ole vielä saatu, näytetään latausnäkymä
                MapLoadingView()
            } else {
                // 2. Kun sijainti on saatu, näytetään kartta
                LetterMapOverlay(
                    userGeoPoint = userGeoPoint,
                    santaPoint = santaPoint,
                    progress = progress,
                    mapCentered = mapCentered,
                    onUpdateMapCentered = { mapCentered = it }
                )

                // 3. Näytetään tilakortti alareunassa
                Box(modifier = Modifier.align(Alignment.BottomCenter)) {
                    DeliveryStatusCard(
                        progress = progress,
                        distanceKm = distanceKm,
                        onBack = onBack
                    )
                }
            }
        }
    }
}