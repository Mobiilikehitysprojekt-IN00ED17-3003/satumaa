package fi.antero.satumaa.ui.components.menu

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Email
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import fi.antero.satumaa.R
import fi.antero.satumaa.ui.components.ModernMenuCard
import fi.antero.satumaa.ui.theme.AppDimensions
import fi.antero.satumaa.ui.theme.Forest
import fi.antero.satumaa.ui.theme.Terracotta

/**
 * Valikon kortit (Iltasatu ja Kirje) sekä niiden kelluva animaatio.
 *
 * @param onStoryClick Toiminto, kun "Lue Iltasatu" valitaan.
 * @param onLetterClick Toiminto, kun "Kirje Joulupukille" valitaan.
 */
@Composable
fun MenuOptions(
    onStoryClick: () -> Unit,
    onLetterClick: () -> Unit
) {
    // Luodaan jatkuva, rauhallinen kellunta-animaatio
    val infiniteTransition = rememberInfiniteTransition(label = "floating")
    val floatAnim by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = AppDimensions.FloatAnimTargetY.value,
        animationSpec = infiniteRepeatable(
            animation = tween(AppDimensions.FloatAnimDuration, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float"
    )

    // Kortti 1: Iltasatu (Vihreä teema)
    ModernMenuCard(
        title = stringResource(R.string.menu_story_title),
        description = stringResource(R.string.menu_story_desc),
        icon = Icons.Default.AutoStories,
        accentColor = Forest,
        offsetY = floatAnim,
        onClick = onStoryClick
    )

    Spacer(Modifier.height(AppDimensions.CardSpacing))

    // Kortti 2: Kirje Joulupukille (Punainen teema)
    // Animoidaan hieman eri tahdissa (0.7x), jotta liike näyttää luonnollisemmalta
    ModernMenuCard(
        title = stringResource(R.string.menu_letter_title),
        description = stringResource(R.string.menu_letter_desc),
        icon = Icons.Default.Email,
        accentColor = Terracotta,
        offsetY = floatAnim * 0.7f,
        onClick = onLetterClick
    )
}