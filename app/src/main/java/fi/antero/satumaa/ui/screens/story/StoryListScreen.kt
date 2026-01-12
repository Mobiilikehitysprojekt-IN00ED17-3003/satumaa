// app/src/main/java/fi/antero/satumaa/ui/screens/story/StoryListScreen.kt
package fi.antero.satumaa.ui.screens.story

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import fi.antero.satumaa.R
import fi.antero.satumaa.ui.components.AppTopBar
import fi.antero.satumaa.ui.navigation.RootRoute

@Composable
fun StoryListScreen(
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    Scaffold { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Image(
                painter = painterResource(id = R.drawable.lampi6),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            AppTopBar(
                title = "Satumaa",
                showBack = true,
                onBack = { onNavigate(RootRoute.Menu.route) },
                onOpenProfile = { onNavigate(RootRoute.Profile.route) },
                modifier = Modifier.align(Alignment.TopCenter)
            )

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Täällä listataan sadut")
            }
        }
    }
}
