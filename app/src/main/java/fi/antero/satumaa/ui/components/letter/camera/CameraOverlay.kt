package fi.antero.satumaa.ui.components.letter.camera

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

@Composable
fun CameraOverlay(
    hintText: String,
    hotness: Float,              // 0..1 (kylmä->kuuma)
    showLetter: Boolean,         // true kun oikeasti löytynyt (tiukka ehto)
    baseOffsetXPx: Int,          // peruspaikka ruudulla (px)
    baseOffsetYPx: Int,          // peruspaikka ruudulla (px)
    onLetterTap: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Haetaan puhelimen kallistus (pieni “leijunta” efekti)
    val tilt by rememberTilt()

    // Kuinka paljon kirje liikkuu kallistuksen mukaan
    val maxOffsetPx = 80
    val tiltOffsetX = (-tilt.x * maxOffsetPx).roundToInt()
    val tiltOffsetY = (tilt.y * maxOffsetPx).roundToInt()

    // Haamu-kynnys: alkaa näkyä vähän ennen löytymistä
    val ghostThreshold = 0.85f

    // Haamu näkyy kun ollaan “melkein kuumana”
    val showGhost = !showLetter && hotness >= ghostThreshold

    // Fade/scale animaatio kirjeelle
    val alphaAnim = remember { Animatable(0f) }
    val scaleAnim = remember { Animatable(0.92f) }

    // Päätetään tavoite-alpha ja tavoite-scale tilanteen mukaan
    val targetAlpha = when {
        showLetter -> 1f
        showGhost -> {
            // Haamu kirkastuu hieman sitä mukaan kun hotness kasvaa
            // hotness 0.85 -> ~0.25, hotness 1.0 -> ~0.55
            val t = ((hotness - ghostThreshold) / (1f - ghostThreshold)).coerceIn(0f, 1f)
            0.25f + (0.30f * t)
        }
        else -> 0f
    }

    val targetScale = when {
        showLetter -> 1f
        showGhost -> 0.96f
        else -> 0.92f
    }

    // Kun tavoite muuttuu, animoidaan pehmeästi uuteen tilaan
    LaunchedEffect(showLetter, showGhost, hotness) {
        // Alpha aina pehmeästi kohti tavoitetta
        alphaAnim.animateTo(targetAlpha, tween(180))

        // Scale pehmeästi kohti tavoitetta
        scaleAnim.animateTo(targetScale, tween(220))
    }

    // Yhdistetään peruspaikka + kallistusliike
    val finalOffset = remember(baseOffsetXPx, baseOffsetYPx, tiltOffsetX, tiltOffsetY) {
        IntOffset(baseOffsetXPx + tiltOffsetX, baseOffsetYPx + tiltOffsetY)
    }

    Box(modifier = modifier.fillMaxSize()) {

        // Alareunan ohjeteksti (ei suuntavihjeitä)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(Color.Black.copy(alpha = 0.35f))
                .padding(16.dp)
        ) {
            Text(
                text = hintText,
                color = Color.White,
                style = MaterialTheme.typography.titleMedium
            )

            // “Fiilis”-teksti ilman vihjeitä
            val mood = when {
                hotness < 0.35f -> "Kylmä"
                hotness < 0.70f -> "Lämpenee"
                hotness < ghostThreshold -> "Kuuma"
                showLetter -> "Löytyi!"
                else -> "Melkein!"
            }

            Spacer(Modifier.height(4.dp))

            Text(
                text = mood,
                color = Color.White.copy(alpha = 0.9f),
                style = MaterialTheme.typography.bodyMedium
            )
        }

        // Piirretään kirje jos se on haamu tai löytynyt (alpha hoitaa näkyvyyden)
        if (alphaAnim.value > 0.01f) {
            FloatingLetter(
                onTap = onLetterTap,
                enabled = showLetter, // Haamukirjettä ei voi klikata, vasta kun on oikeasti löytynyt
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset { finalOffset }
                    .alpha(alphaAnim.value)
                    .scale(scaleAnim.value)
            )
        }
    }
}

// Kirje-elementti, jota voi napauttaa vasta kun se on oikeasti löytynyt
@Composable
private fun FloatingLetter(
    onTap: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(width = 220.dp, height = 140.dp)
            .background(
                color = Color(0xFFF7F1E3),
                shape = RoundedCornerShape(18.dp)
            )
            .then(
                if (enabled) Modifier.clickable(onClick = onTap) else Modifier
            )
            .padding(14.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = if (enabled) "📜 Kirje löytyi!" else "📜 Näet jotain…",
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFF2B2B2B)
            )
            Text(
                text = if (enabled) "Napauta avataksesi" else "Käänny vielä vähän…",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF2B2B2B)
            )
        }
    }
}
