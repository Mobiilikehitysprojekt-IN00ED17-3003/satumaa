package fi.antero.satumaa.ui.components.letter.camera

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

@Composable
fun CameraOverlay(
    hintText: String,
    showLetter: Boolean,
    onLetterTap: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Haetaan puhelimen kallistus
    val tilt by rememberTilt()

    // Kuinka paljon kirje liikkuu kallistuksen mukaan
    val maxOffsetPx = 80
    val offsetX = (-tilt.x * maxOffsetPx).roundToInt()
    val offsetY = (tilt.y * maxOffsetPx).roundToInt()

    Box(modifier = modifier.fillMaxSize()) {

        // Alareunan ohjeteksti
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
        }

        // Leijuva kirje näkyy vain kun se on löytynyt
        if (showLetter) {
            FloatingLetter(
                onTap = onLetterTap,
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset { IntOffset(offsetX, offsetY) }
            )
        }
    }
}

// Yksinkertainen kirje-elementti
@Composable
private fun FloatingLetter(
    onTap: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(width = 220.dp, height = 140.dp)
            .clickable(onClick = onTap)
            .background(
                color = Color(0xFFF7F1E3),
                shape = RoundedCornerShape(18.dp)
            )
            .padding(14.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "📜 Kirje löytyi!",
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFF2B2B2B)
            )
            Text(
                text = "Napauta avataksesi",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF2B2B2B)
            )
        }
    }
}
