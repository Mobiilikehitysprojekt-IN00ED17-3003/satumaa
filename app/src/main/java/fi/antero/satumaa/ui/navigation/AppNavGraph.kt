package fi.antero.satumaa.ui.navigation

import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import fi.antero.satumaa.ui.screens.auth.LoginScreen
import fi.antero.satumaa.ui.screens.letter.LetterCameraScreen
import fi.antero.satumaa.ui.screens.letter.LetterFlowScreen
import fi.antero.satumaa.ui.screens.letter.LetterListScreen
import fi.antero.satumaa.ui.screens.letter.LetterMapScreen
import fi.antero.satumaa.ui.screens.menu.MenuScreen
import fi.antero.satumaa.ui.screens.onboarding.OnboardingScreen
import fi.antero.satumaa.ui.screens.profile.ProfileScreen
import fi.antero.satumaa.ui.screens.story.StoryListScreen
import fi.antero.satumaa.ui.screens.story.StoryScreen
import fi.antero.satumaa.viewmodel.letter.LetterViewModel

@Composable
fun AppNavGraph(
    navController: NavHostController,
    startDestination: String,
) {
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
        startDestination = startDestination
    ) {

        // --- AUTH & ONBOARDING ---

        composable(RootRoute.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(RootRoute.Onboarding.route) {
                        popUpTo(RootRoute.Login.route) { inclusive = true }
                    }
                }
            )
        }

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

        // --- SATU-OSIO ---

        composable(
            route = RootRoute.Story.route + "?storyId={storyId}",
            arguments = listOf(navArgument("storyId") { nullable = true })
        ) { entry ->
            val storyId = entry.arguments?.getString("storyId")
            StoryScreen(
                userName = adventurerName,
                storyId = storyId,
                onNavigate = navigate
            )
        }

        composable(RootRoute.StoryList.route) {
            StoryListScreen(
                onNavigateBack = { navController.popBackStack() },
                onStoryClick = { id ->
                    navController.navigate(RootRoute.Story.createRoute(id))
                }
            )
        }

        // --- KIRJEET ---

        composable(
            route = RootRoute.Letter.route + "?letterId={letterId}",
            arguments = listOf(navArgument("letterId") { nullable = true })
        ) { entry ->
            val letterId = entry.arguments?.getString("letterId")
            LetterFlowScreen(
                currentRoute = currentRoute,
                onNavigate = navigate,
                userName = adventurerName,
                letterId = letterId
            )
        }

        composable(RootRoute.LetterList.route) {
            LetterListScreen(
                onNavigateBack = { navController.popBackStack() },
                onLetterClick = { letterId ->
                    navController.navigate(RootRoute.Letter.route + "?letterId=$letterId")
                }
            )
        }

        composable(
            route = "${LetterRoutes.CAMERA}/{letterId}",
            arguments = listOf(navArgument("letterId") { type = NavType.StringType })
        ) { backStackEntry ->
            val letterId = backStackEntry.arguments?.getString("letterId")

            val viewModel = hiltViewModel<LetterViewModel>()

            LaunchedEffect(letterId) {
                if (letterId != null) {
                    viewModel.loadLetter(letterId)
                }
            }

            LetterCameraScreen(
                onFoundLetter = {
                    viewModel.markLetterAsOpened()
                    navController.popBackStack()
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(LetterRoutes.CAMERA) {
            val viewModel = hiltViewModel<LetterViewModel>()
            LetterCameraScreen(
                onFoundLetter = {
                    viewModel.markLetterAsOpened()
                    navController.popBackStack()
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = "${RootRoute.LetterMap.route}/{letterId}",
            arguments = listOf(navArgument("letterId") { type = NavType.StringType })
        ) { backStackEntry ->
            val letterId = backStackEntry.arguments?.getString("letterId") ?: ""
            LetterMapScreen(
                letterId = letterId,
                onBack = { navController.popBackStack() }
            )
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
