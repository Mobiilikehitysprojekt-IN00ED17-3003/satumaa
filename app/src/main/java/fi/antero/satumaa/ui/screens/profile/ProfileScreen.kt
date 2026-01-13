package fi.antero.satumaa.ui.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import fi.antero.satumaa.ui.components.AppBottomBar
import fi.antero.satumaa.ui.components.AppPageLayout
import fi.antero.satumaa.ui.components.AppTopBar
import fi.antero.satumaa.ui.navigation.RootRoute
import fi.antero.satumaa.ui.theme.AppDimensions
import fi.antero.satumaa.ui.theme.LocalAppImages
import fi.antero.satumaa.ui.theme.StorybookPaper

@Composable
fun ProfileScreen(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    onLogout: () -> Unit
) {
    AppPageLayout(
        backgroundImageRes = LocalAppImages.current.profileBackground,
        topBar = {
            AppTopBar(
                overrideTitle = "Profiili",
                showBack = true,
                onBack = { onNavigate(RootRoute.Menu.route) }
            )
        },
        bottomBar = { AppBottomBar(currentRoute, onNavigate) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = AppDimensions.ScreenPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                "Profiili",
                style = MaterialTheme.typography.headlineLarge,
                color = StorybookPaper
            )
            Spacer(Modifier.height(AppDimensions.CardSpacing))
            Button(onClick = onLogout) {
                Text("Kirjaudu ulos")
            }
        }
    }
}