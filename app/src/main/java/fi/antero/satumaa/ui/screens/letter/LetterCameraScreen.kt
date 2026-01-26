package fi.antero.satumaa.ui.screens.letter

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import fi.antero.satumaa.ui.components.letter.camera.CameraOverlay
import fi.antero.satumaa.ui.components.letter.camera.LetterOpenAnimation
import kotlinx.coroutines.delay

// Kameranäkymä, jossa käyttäjä etsii pukin vastausta AR-tyylisesti
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LetterCameraScreen(
    onFoundLetter: () -> Unit, // Kutsutaan kun kirje on löydetty
    onBack: () -> Unit         // Paluu edelliseen näkymään
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Tarkistetaan onko kameraoikeus jo annettu
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    // Pyytää kameraoikeuden käyttäjältä
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasCameraPermission = granted
    }

    // Etsintään liittyvät tilat
    var scanProgress by remember { mutableFloatStateOf(0f) } // 0–100 %
    var showLetter by remember { mutableStateOf(false) }     // Näytetäänkö kirje
    var opening by remember { mutableStateOf(false) }        // Avataanko kirje

    // Pyydetään kameraoikeus heti näkymän avautuessa
    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    // Simuloitu "etsintä": progress kasvaa ja lopuksi kirje ilmestyy
    LaunchedEffect(hasCameraPermission) {
        if (hasCameraPermission) {
            scanProgress = 0f
            showLetter = false
            opening = false

            // Progress kestää noin 4 sekuntia
            val steps = 40
            repeat(steps) {
                delay(100)
                scanProgress = ((it + 1) / steps.toFloat()).coerceIn(0f, 1f)
            }

            // Kun etsintä valmis, näytetään kirje
            showLetter = true
        }
    }

    // Ohjeteksti käyttäjälle
    val hintText = when {
        !showLetter -> "Liikuta puhelinta ja etsi kirjettä…"
        else -> "Kirje löytyi! Napauta sitä."
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Etsi vastaus") },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text("Takaisin") }
                }
            )
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            if (hasCameraPermission) {

                // Kameran esikatselu
                CameraPreview(
                    modifier = Modifier.fillMaxSize(),
                    onBind = { previewView ->
                        bindCameraPreview(
                            context = context,
                            lifecycleOwner = lifecycleOwner,
                            previewView = previewView
                        )
                    }
                )

                // Overlay kameran päällä (ohje + leijuva kirje)
                CameraOverlay(
                    hintText = hintText,
                    showLetter = showLetter,
                    onLetterTap = {
                        showLetter = false
                        opening = true
                    },
                    modifier = Modifier.fillMaxSize()
                )

                // Näytetään progressbar vain etsinnän aikana
                if (!showLetter && !opening) {
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 70.dp)
                    ) {
                        LinearProgressIndicator(
                            progress = { scanProgress },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "${(scanProgress * 100).toInt()}% skannattu",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                // Kirjeen avautumisanimaatio
                LetterOpenAnimation(
                    visible = opening,
                    onFinished = {
                        opening = false
                        onFoundLetter()
                    },
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                // Näytetään jos kameraoikeutta ei ole annettu
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Kameraoikeus tarvitaan, jotta voit etsiä kirjettä.")
                    Button(onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) }) {
                        Text("Salli kamera")
                    }
                    OutlinedButton(onClick = onBack) {
                        Text("Takaisin")
                    }
                }
            }
        }
    }
}

// Android View joka näyttää CameraX-esikatselun Composessa
@Composable
private fun CameraPreview(
    modifier: Modifier = Modifier,
    onBind: (PreviewView) -> Unit
) {
    AndroidView(
        modifier = modifier,
        factory = { ctx -> PreviewView(ctx) },
        update = { previewView -> onBind(previewView) }
    )
}

// Yhdistää kameran elinkaaren Compose-näkymään
private fun bindCameraPreview(
    context: Context,
    lifecycleOwner: androidx.lifecycle.LifecycleOwner,
    previewView: PreviewView
) {
    val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
    cameraProviderFuture.addListener({
        val cameraProvider = cameraProviderFuture.get()

        val preview = Preview.Builder().build().apply {
            setSurfaceProvider(previewView.surfaceProvider)
        }

        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview
            )
        } catch (_: Exception) {
            // Virhe kameran käynnistyksessä (ei kaadeta sovellusta)
        }
    }, ContextCompat.getMainExecutor(context))
}
