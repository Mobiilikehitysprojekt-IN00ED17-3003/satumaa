package fi.antero.satumaa.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.*
import fi.antero.satumaa.ui.navigation.RootRoute
import fi.antero.satumaa.ui.screens.auth.LoginScreen
import fi.antero.satumaa.ui.screens.menu.MenuScreen
import fi.antero.satumaa.ui.screens.story.StoryListScreen
import fi.antero.satumaa.ui.screens.letter.LetterFlowScreen
import fi.antero.satumaa.ui.screens.profile.ProfileScreen

@Composable
fun SatumaaApp() {
    val navController = rememberNavController()
    val backStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry.value?.destination?.route

    val navigate: (String) -> Unit = { route ->
        navController.navigate(route) {
            launchSingleTop = true
            restoreState = true
        }
    }

    NavHost(
        navController = navController,
        startDestination = RootRoute.Login.route
    ) {

        composable(RootRoute.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(RootRoute.Menu.route) {
                        popUpTo(RootRoute.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(RootRoute.Menu.route) {
            MenuScreen(currentRoute, navigate)
        }

        composable(RootRoute.Story.route) {
            StoryListScreen(currentRoute, navigate)
        }

        composable(RootRoute.Letter.route) {
            LetterFlowScreen(currentRoute, navigate)
        }

        composable(RootRoute.Profile.route) {
            ProfileScreen(
                currentRoute = currentRoute,
                onNavigate = navigate,
                onLogout = {
                    navController.navigate(RootRoute.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}
