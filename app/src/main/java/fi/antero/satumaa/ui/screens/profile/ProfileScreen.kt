package fi.antero.satumaa.ui.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import fi.antero.satumaa.ui.components.AppBottomBar
import fi.antero.satumaa.ui.components.AppPageLayout
import fi.antero.satumaa.ui.components.AppTopBar
import fi.antero.satumaa.ui.navigation.RootRoute
import fi.antero.satumaa.ui.screens.profile.math.ProfileMathSection
import fi.antero.satumaa.ui.theme.LocalAppImages

@Composable
fun ProfileScreen(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    onLogout: () -> Unit
) {
    val user = Firebase.auth.currentUser
    val email = user?.email ?: "Ei sähköpostia"

    AppPageLayout(
        backgroundImageRes = LocalAppImages.current.profileBackground,
        topBar = {
            AppTopBar(
                overrideTitle = "Profiili",
                showBack = true,
                onBack = { onNavigate(RootRoute.Menu.route) }
            )
        },
        bottomBar = {
            AppBottomBar(currentRoute, onNavigate)
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {

            Card(
                colors = CardDefaults.cardColors(

                    containerColor = Color(0xFFFFF3E6).copy(alpha = 0.9f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Kirjautunut käyttäjä:",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFF1B1B1F)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = email,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color(0xFF2E6B5B)
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = {
                            Firebase.auth.signOut()
                            onLogout()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFB4573A),
                            contentColor = Color(0xFFFFF3E6)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Kirjaudu ulos")
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- TILASTOT ---
            ProfileMathSection()

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}