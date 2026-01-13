package fi.antero.satumaa.ui.screens.story

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import fi.antero.satumaa.ui.components.AppPageLayout
import fi.antero.satumaa.ui.components.AppTopBar
import fi.antero.satumaa.ui.navigation.RootRoute
import fi.antero.satumaa.ui.theme.AppDimensions
import fi.antero.satumaa.ui.theme.LocalAppImages
import fi.antero.satumaa.ui.theme.StorybookPaper

@Composable
fun StoryListScreen(
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    AppPageLayout(
        backgroundImageRes = LocalAppImages.current.storyListBackground,
        topBar = {
            AppTopBar(
                showBack = true,
                onBack = { onNavigate(RootRoute.Menu.route) },
                onOpenProfile = { onNavigate(RootRoute.Profile.route) }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = AppDimensions.ScreenPadding),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "Täällä listataan sadut",
                style = MaterialTheme.typography.bodyLarge,
                color = StorybookPaper
            )
        }
    }
}