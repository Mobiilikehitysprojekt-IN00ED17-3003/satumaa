package fi.antero.satumaa.ui.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import fi.antero.satumaa.R
import fi.antero.satumaa.ui.components.AppBottomBar
import fi.antero.satumaa.ui.components.AppPageLayout
import fi.antero.satumaa.ui.components.AppTopBar
import fi.antero.satumaa.ui.components.profile.UserInfoCard
import fi.antero.satumaa.ui.navigation.RootRoute
import fi.antero.satumaa.ui.screens.profile.math.ProfileMathSection
import fi.antero.satumaa.ui.theme.LocalAppImages

/**
 * Profiilinäkymä (ProfileScreen).
 *
 * Näyttää käyttäjän tiedot ja tarjoaa pääsyn asetuksiin sekä tilastoihin.
 * Sisältää myös ProfileMathSection-komponentin, joka hallinnoi matematiikan edistymistä.
 */
@Composable
fun ProfileScreen(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    onLogout: () -> Unit
) {
    // Haetaan käyttäjä Firebasesta
    val user = Firebase.auth.currentUser
    val email = user?.email ?: stringResource(R.string.profile_no_email)

    AppPageLayout(
        backgroundImageRes = LocalAppImages.current.profileBackground,
        topBar = {
            AppTopBar(
                overrideTitle = stringResource(R.string.profile_title),
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

            // 1. Käyttäjätietokortti (sis. Uloskirjautuminen)
            UserInfoCard(
                email = email,
                onLogout = {
                    Firebase.auth.signOut()
                    onLogout()
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 2. Tilastot (Matematiikka) - Tämä pidetään erillisenä kokonaisuutena
            ProfileMathSection()

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}