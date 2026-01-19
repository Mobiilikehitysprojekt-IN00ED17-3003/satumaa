package fi.antero.satumaa.ui

import androidx.compose.runtime.*
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import fi.antero.satumaa.ui.navigation.RootRoute
import fi.antero.satumaa.ui.screens.auth.LoginScreen
import fi.antero.satumaa.ui.screens.menu.MenuScreen
import fi.antero.satumaa.ui.screens.story.StoryScreen
import fi.antero.satumaa.ui.screens.story.StoryListScreen
import fi.antero.satumaa.ui.screens.letter.LetterFlowScreen
import fi.antero.satumaa.ui.screens.profile.ProfileScreen
import fi.antero.satumaa.ui.screens.onboarding.OnboardingScreen

@Composable
fun SatumaaApp() {
    val navController = rememberNavController()
    val backStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry.value?.destination?.route

    var adventurerName by remember { mutableStateOf("Seikkailija") }

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
        // --- KIRJAUTUMINEN ---
        composable(RootRoute.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(RootRoute.Onboarding.route) {
                        popUpTo(RootRoute.Login.route) { inclusive = true }
                    }
                }
            )
        }

        // --- ONBOARDING (Nimen kysyminen) ---
        composable(RootRoute.Onboarding.route) {
            OnboardingScreen(
                onNameSubmitted = { name ->
                    adventurerName = name
                    navController.navigate(RootRoute.Menu.route) {
                        popUpTo(RootRoute.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        // --- PÄÄVALIKKO ---
        composable(RootRoute.Menu.route) {
            MenuScreen(
                currentRoute = currentRoute,
                userName = adventurerName,
                onNavigate = navigate
            )
        }

        // --- YKSITTÄINEN SATU (ID:llä) ---
        composable(
            route = "story?storyId={storyId}",
            arguments = listOf(navArgument("storyId") { nullable = true })
        ) { entry ->
            val storyId = entry.arguments?.getString("storyId")
            StoryScreen(
                userName = adventurerName,
                storyId = storyId,
                onNavigate = navigate
            )
        }

        // --- SATULISTA ---
        composable(RootRoute.StoryList.route) {
            StoryListScreen(
                onNavigateBack = { navController.popBackStack() },
                onStoryClick = { id ->
                    // Navigoidaan story-ruutuun ID:n kanssa
                    navController.navigate("story?storyId=$id")
                }
            )
        }

        // --- KIRJEIDEN LUONTI ---
        composable(RootRoute.Letter.route) {
            LetterFlowScreen(currentRoute, navigate)
        }

        // --- PROFIILI ---
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