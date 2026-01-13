package fi.antero.satumaa.ui.screens.menu

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Email
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fi.antero.satumaa.ui.components.AppPageLayout
import fi.antero.satumaa.ui.components.AppTopBar
import fi.antero.satumaa.ui.components.ModernMenuCard
import fi.antero.satumaa.ui.navigation.RootRoute
import fi.antero.satumaa.ui.theme.*

@Composable
fun MenuScreen(
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
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

    AppPageLayout(
        backgroundImageRes = LocalAppImages.current.menuBackground,
        topBar = {
            AppTopBar(
                onOpenProfile = { onNavigate(RootRoute.Profile.route) }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = AppDimensions.ScreenPadding, vertical = 40.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ModernMenuCard(
                title = "Lue Iltasatu",
                description = "Taikuutta jokaiseen iltaan",
                icon = Icons.Default.AutoStories,
                accentColor = Forest,
                offsetY = floatAnim,
                onClick = { onNavigate(RootRoute.Story.route) }
            )

            Spacer(Modifier.height(AppDimensions.CardSpacing))

            ModernMenuCard(
                title = "Kirje Joulupukille",
                description = "Lähetä terveisesi Korvatunturille",
                icon = Icons.Default.Email,
                accentColor = Terracotta,
                offsetY = floatAnim * 0.7f,
                onClick = { onNavigate(RootRoute.Letter.route) }
            )

            Spacer(Modifier.height(AppDimensions.BottomPadding))
        }
    }
}