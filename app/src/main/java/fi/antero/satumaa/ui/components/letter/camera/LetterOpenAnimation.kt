package fi.antero.satumaa.ui.components.letter.camera

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Animaatio, joka näyttää kirjeen avautuvan kameran päällä
@Composable
fun LetterOpenAnimation(
    visible: Boolean,          // Näytetäänkö animaatio
    onFinished: () -> Unit,    // Kutsutaan kun animaatio on valmis
    modifier: Modifier = Modifier
) {
    // Jos animaatio ei ole aktiivinen, ei piirretä mitään
    if (!visible) return

    // Coroutine-scope animaatioita varten
    val scope = rememberCoroutineScope()

    // Skaala kirjeen "pop in" -efektille
    val scale = remember { Animatable(0.85f) }

    // Läpinäkyvyys fade in / fade out -efektiin
    val alpha = remember { Animatable(0f) }

    // Käynnistää animaation kun visible muuttuu
    LaunchedEffect(visible) {
        // Fade in
        scope.launch { alpha.animateTo(1f, tween(180)) }

        // Pieni zoom sisään ja takaisin
        scale.animateTo(1.05f, tween(220))
        scale.animateTo(1.0f, tween(120))

        // Lyhyt odotus, jotta animaatio tuntuu "avautumiselta"
        delay(550)

        // Fade out
        alpha.animateTo(0f, tween(160))

        // Ilmoitetaan että animaatio on valmis
        onFinished()
    }

    // Tumma tausta kameran päälle
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.35f * alpha.value)),
        contentAlignment = Alignment.Center
    ) {
        // Kirjeen visuaalinen kortti
        Box(
            modifier = Modifier
                .scale(scale.value)
                .size(width = 260.dp, height = 170.dp)
                .background(
                    color = Color(0xFFF7F1E3),
                    shape = RoundedCornerShape(22.dp)
                )
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Otsikkoteksti
                Text(
                    text = "📬 Kirje aukeaa…",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color(0xFF2B2B2B)
                )

                // Alateksti
                Text(
                    text = "Hetkinen…",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF2B2B2B)
                )
            }
        }
    }
}
