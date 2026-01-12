package fi.antero.satumaa.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import fi.antero.satumaa.ui.navigation.RootRoute

@Composable
fun AppBottomBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    NavigationBar {
        NavigationBarItem(
            selected = currentRoute == RootRoute.Menu.route,
            onClick = { onNavigate(RootRoute.Menu.route) },
            icon = { Icon(Icons.Default.Home, null) },
            label = { Text("Koti") }
        )
        NavigationBarItem(
            selected = currentRoute == RootRoute.Story.route,
            onClick = { onNavigate(RootRoute.Story.route) },
            icon = { Icon(Icons.Default.AutoStories, null) },
            label = { Text("Satu") }
        )
        NavigationBarItem(
            selected = currentRoute == RootRoute.Letter.route,
            onClick = { onNavigate(RootRoute.Letter.route) },
            icon = { Icon(Icons.Default.Email, null) },
            label = { Text("Kirje") }
        )
        NavigationBarItem(
            selected = currentRoute == RootRoute.Profile.route,
            onClick = { onNavigate(RootRoute.Profile.route) },
            icon = { Icon(Icons.Default.Person, null) },
            label = { Text("Profiili") }
        )
    }
}
