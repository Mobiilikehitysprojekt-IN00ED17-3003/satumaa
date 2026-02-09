package fi.antero.satumaa.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fi.antero.satumaa.R
import fi.antero.satumaa.ui.theme.StorybookPaper

/**
 * Yleiskäyttöinen latausnäkymä.
 *
 * Näyttää pyörivän indikaattorin ja tekstin. Käytetään, kun sovellus hakee dataa
 * tai suorittaa pitkäkestoista operaatiota (esim. sadun generointi).
 *
 * @param modifier Komponentin muokkain.
 * @param text Näytettävä teksti (oletuksena "Taikuutta ladataan...").
 */
@Composable
fun LoadingView(
    modifier: Modifier = Modifier,
    text: String = stringResource(R.string.loading_default_text)
) {
    // Luodaan jatkuva animaatio pyöritystä varten
    val infiniteTransition = rememberInfiniteTransition(label = "loading")

    // Animoidaan rotaatioarvoa 0 -> 360 astetta
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing), // Kesto 1.5s, tasainen nopeus
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Pyörivä ympyrä
        CircularProgressIndicator(
            modifier = Modifier
                .size(64.dp)
                .rotate(rotation), // Sovelletaan animaatiota
            color = StorybookPaper,
            strokeWidth = 4.dp
        )

        Spacer(Modifier.height(24.dp))

        // Latausteksti
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 18.sp,
                letterSpacing = 1.sp
            ),
            color = StorybookPaper
        )
    }
}