package fi.antero.satumaa.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext


private val LightColorScheme = lightColorScheme(
    primary = Forest,
    onPrimary = StorybookPaper,

    secondary = Sky,
    onSecondary = StorybookPaper,

    tertiary = Terracotta,
    onTertiary = StorybookPaper,

    background = StorybookPaper,
    onBackground = Ink,

    surface = StorybookPaper,
    onSurface = Ink,

    surfaceVariant = StorybookPaper2,
    onSurfaceVariant = InkSoft,

    outline = OutlineSoft,
    scrim = OverlayScrim
)


private val DarkColorScheme = darkColorScheme(
    primary = Forest,
    onPrimary = StorybookPaper,
    secondary = Sky,
    onSecondary = StorybookPaper,
    tertiary = Terracotta,
    onTertiary = StorybookPaper,
    background = Color(0xFF121214),
    onBackground = Color(0xFFEAE6E1),
    surface = Color(0xFF1A1A1E),
    onSurface = Color(0xFFEAE6E1),
    surfaceVariant = Color(0xFF2A2A31),
    onSurfaceVariant = Color(0xFFD6D0C9),
    outline = Color(0xFF6A6460),
    scrim = OverlayScrim
)

@Composable
fun SatumaaTheme(

    darkTheme: Boolean = false,
    dynamicColor: Boolean = false,

    content: @Composable () -> Unit
) {

    val colorScheme = LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}