// app/src/main/java/fi/antero/satumaa/ui/screens/profile/ProfileScreen.kt
package fi.antero.satumaa.ui.screens.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import fi.antero.satumaa.R
import fi.antero.satumaa.ui.components.AppBottomBar
import fi.antero.satumaa.ui.components.AppTopBar
import fi.antero.satumaa.ui.navigation.RootRoute

@Composable
fun ProfileScreen(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    onLogout: () -> Unit
) {
    Scaffold(
        topBar = {
            AppTopBar(
                title = "Profiili",
                showBack = true,
                onBack = { onNavigate(RootRoute.Menu.route) },
                modifier = Modifier
            )
        },
        bottomBar = { AppBottomBar(currentRoute, onNavigate) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ratsu),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Profiili")
                Spacer(Modifier.height(16.dp))
                Button(onClick = onLogout) {
                    Text("Kirjaudu ulos")
                }
            }
        }
    }
}
