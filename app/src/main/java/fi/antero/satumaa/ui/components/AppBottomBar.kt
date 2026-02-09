package fi.antero.satumaa.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import fi.antero.satumaa.R
import fi.antero.satumaa.ui.navigation.RootRoute

/**
 * Sovelluksen alapalkki päänavigaatiolle.
 *
 * @param currentRoute Nykyinen aktiivinen reitti.
 * @param onNavigate Callback navigoinnille.
 */
@Composable
fun AppBottomBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    NavigationBar {
        // Koti
        NavigationBarItem(
            selected = currentRoute == RootRoute.Menu.route,
            onClick = { onNavigate(RootRoute.Menu.route) },
            icon = { Icon(Icons.Default.Home, null) },
            label = { Text(stringResource(R.string.nav_home)) }
        )

        // Satu
        NavigationBarItem(
            selected = currentRoute == RootRoute.Story.route,
            onClick = { onNavigate(RootRoute.Story.route) },
            icon = { Icon(Icons.Default.AutoStories, null) },
            label = { Text(stringResource(R.string.nav_story)) }
        )

        // Kirje
        NavigationBarItem(
            selected = currentRoute == RootRoute.Letter.route,
            onClick = { onNavigate(RootRoute.Letter.route) },
            icon = { Icon(Icons.Default.Email, null) },
            label = { Text(stringResource(R.string.nav_letter)) }
        )

        // Profiili
        NavigationBarItem(
            selected = currentRoute == RootRoute.Profile.route,
            onClick = { onNavigate(RootRoute.Profile.route) },
            icon = { Icon(Icons.Default.Person, null) },
            label = { Text(stringResource(R.string.nav_profile)) }
        )
    }
}