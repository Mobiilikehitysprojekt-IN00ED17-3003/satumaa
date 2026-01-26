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
import fi.antero.satumaa.ui.components.letter.camera.rememberHeadingDegrees
import kotlinx.coroutines.delay
import kotlin.math.abs
import kotlin.random.Random

// Kameranäkymä, jossa käyttäjä etsii pukin vastausta AR-tyylisesti (vaihtoehto 2 + kuuma–kylmä)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LetterCameraScreen(
    onFoundLetter: () -> Unit, // Kutsutaan kun kirje on löydetty ja avattu
    onBack: () -> Unit         // Paluu edelliseen näkymään
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Luetaan puhelimen katsesuunta (yaw/heading)
    val headingDeg by rememberHeadingDegrees()

    // Tarkistetaan onko kameraoikeus jo annettu
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                    PackageManager.PERMISSION_GRANTED
        )
    }

    // Pyytää kameraoikeuden käyttäjältä
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasCameraPermission = granted
    }

    // Kirjeen etsimiseen liittyvät tilat
    var opening by remember { mutableStateOf(false) }            // Näytetäänkö avausanimaatio
    var targetHeading by remember { mutableStateOf<Float?>(null) } // Kirjeen "piilosuunta" asteina
    var toleranceDeg by remember { mutableFloatStateOf(12f) }    // Kuinka tarkasti pitää osua suuntaan (vaikea)
    var startedAtMs by remember { mutableLongStateOf(0L) }       // Milloin etsintä alkoi (anti-stuck)

    // Kirjeen peruspaikka ruudulla (ei aina keskellä)
    var baseOffsetXPx by remember { mutableIntStateOf(0) }
    var baseOffsetYPx by remember { mutableIntStateOf(0) }

    // Pyydetään kameraoikeus heti näkymän avautuessa
    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    // Kun kamera on käytössä, alustetaan etsintä (arvotaan targetHeading + kirjeen ruutusijainti)
    LaunchedEffect(hasCameraPermission) {
        if (!hasCameraPermission) return@LaunchedEffect

        // Asetetaan aloitusaika (anti-stuck)
        startedAtMs = System.currentTimeMillis()

        // Palautetaan vaikeus lähtöarvoon
        toleranceDeg = 12f

        // Arvotaan kirjeen suunta niin, että se voi olla myös "selän takana"
        // (offset 100..260 astetta tarkoittaa usein selän/sivun suuntaa)
        val offset = Random.nextInt(100, 261).toFloat()
        targetHeading = normalize360(headingDeg + offset)

        // Arvotaan kirjeen perusruutusijainti (px) jotta se ei ole aina keskellä
        baseOffsetXPx = Random.nextInt(-140, 141)
        baseOffsetYPx = Random.nextInt(-120, 81)
    }

    // Anti-stuck: jos käyttäjä ei löydä pitkään aikaan, helpotetaan vähän (ei vihjetekstejä)
    LaunchedEffect(hasCameraPermission, targetHeading) {
        if (!hasCameraPermission || targetHeading == null) return@LaunchedEffect

        while (true) {
            delay(500)

            // Jos avaus menossa, ei säädetä vaikeutta
            if (opening) continue

            val elapsed = System.currentTimeMillis() - startedAtMs

            // Helpotetaan asteittain 15s ja 25s kohdalla (mutta ei tehdä tästä "liian helppoa")
            toleranceDeg = when {
                elapsed > 25_000 -> 24f
                elapsed > 15_000 -> 18f
                else -> 12f
            }
        }
    }

    // Lasketaan kuuma–kylmä (0..1) ja löytyminen kulmaeron perusteella
    val angleDiff = remember(headingDeg, targetHeading, toleranceDeg) {
        val t = targetHeading ?: return@remember 180f
        shortestAngleDiffDeg(headingDeg, t) // 0..180
    }

    // Hotness: 0 = kylmä (180° väärässä), 1 = kuuma (0° oikein)
    val hotness = remember(angleDiff) {
        (1f - (angleDiff / 180f)).coerceIn(0f, 1f)
    }

    // Kirje näkyy vasta kun suunta on tarpeeksi lähellä (tämä tekee "etsimisfiiliksen")
    val showLetter = (targetHeading != null) && (angleDiff <= toleranceDeg) && !opening

    // Ohjeteksti (ei anneta suuntavihjettä)
    val hintText = if (!showLetter) "Etsi kirjettä kameralla…" else "Kirje löytyi! Napauta sitä."

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
                // Kameran esikatselu (CameraX)
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
                    hotness = hotness,                   // kuuma–kylmä arvo 0..1
                    showLetter = showLetter,             // kirje näkyy vasta "kuumana"
                    baseOffsetXPx = baseOffsetXPx,       // kirjeen perusruutusijainti
                    baseOffsetYPx = baseOffsetYPx,
                    onLetterTap = {
                        // Kun kirjettä napautetaan, aloitetaan avaus
                        opening = true
                    },
                    modifier = Modifier.fillMaxSize()
                )

                // Kuuma–kylmä palkki näkyy heti etsinnän alusta (koko ajan)
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 74.dp) // overlayn yläpuolelle
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Kylmä", style = MaterialTheme.typography.bodyMedium)
                        Text("Kuuma", style = MaterialTheme.typography.bodyMedium)
                    }

                    Spacer(Modifier.height(6.dp))

                    LinearProgressIndicator(
                        progress = { hotness },
                        modifier = Modifier.fillMaxWidth()
                    )
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

// Laskee pienimmän kulmaeron 0..180 kahden headingin välillä
private fun shortestAngleDiffDeg(a: Float, b: Float): Float {
    val diff = abs(a - b) % 360f
    return if (diff > 180f) 360f - diff else diff
}

// Normalisoi asteet 0..360
private fun normalize360(deg: Float): Float {
    var d = deg % 360f
    if (d < 0f) d += 360f
    return d
}
