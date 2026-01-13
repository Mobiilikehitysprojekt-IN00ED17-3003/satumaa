package fi.antero.satumaa.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import fi.antero.satumaa.R

@Immutable
data class AppImages(
    val menuBackground: Int = R.drawable.sade,
    val storyListBackground: Int = R.drawable.lampi6,
    val profileBackground: Int = R.drawable.ratsu,
    val letterBackground: Int = R.drawable.lumi,
    val authBackground: Int = R.drawable.kohti_taikalinnaa
)

val LocalAppImages = staticCompositionLocalOf { AppImages() }