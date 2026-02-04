package fi.antero.satumaa.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember as rememberCompose
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import androidx.navigation.navigation
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

private const val LETTER_GRAPH = "letter_graph"

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

        composable(RootRoute.Menu.route) {
            MenuScreen(
                currentRoute = currentRoute,
                userName = adventurerName,
                onNavigate = navigate
            )
        }

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

        navigation(
            route = LETTER_GRAPH,
            startDestination = RootRoute.Letter.route + "?letterId={letterId}"
        ) {

            composable(
                route = RootRoute.Letter.route + "?letterId={letterId}",
                arguments = listOf(navArgument("letterId") { nullable = true })
            ) { entry ->
                val parentEntry = rememberCompose(entry) { navController.getBackStackEntry(LETTER_GRAPH) }
                val vm = hiltViewModel<LetterViewModel>(parentEntry)

                val letterId = entry.arguments?.getString("letterId")
                LetterFlowScreen(
                    currentRoute = currentRoute,
                    onNavigate = navigate,
                    userName = adventurerName,
                    letterId = letterId,
                    vm = vm
                )
            }

            composable(RootRoute.LetterList.route) { entry ->
                val parentEntry = rememberCompose(entry) { navController.getBackStackEntry(LETTER_GRAPH) }
                hiltViewModel<LetterViewModel>(parentEntry)

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
            ) { entry ->
                val parentEntry = rememberCompose(entry) { navController.getBackStackEntry(LETTER_GRAPH) }
                val vm = hiltViewModel<LetterViewModel>(parentEntry)

                val letterId = entry.arguments?.getString("letterId")

                LaunchedEffect(letterId) {
                    if (letterId != null) vm.loadLetter(letterId)
                }

                LetterCameraScreen(
                    onFoundLetter = {
                        vm.markLetterAsOpened()
                        navController.popBackStack()
                    },
                    onBack = { navController.popBackStack() }
                )
            }

            composable(LetterRoutes.CAMERA) { entry ->
                val parentEntry = rememberCompose(entry) { navController.getBackStackEntry(LETTER_GRAPH) }
                val vm = hiltViewModel<LetterViewModel>(parentEntry)

                LetterCameraScreen(
                    onFoundLetter = {
                        vm.markLetterAsOpened()
                        navController.popBackStack()
                    },
                    onBack = { navController.popBackStack() }
                )
            }

            composable(
                route = "${RootRoute.LetterMap.route}/{letterId}",
                arguments = listOf(navArgument("letterId") { type = NavType.StringType })
            ) { entry ->
                val parentEntry = rememberCompose(entry) { navController.getBackStackEntry(LETTER_GRAPH) }
                val vm = hiltViewModel<LetterViewModel>(parentEntry)

                val letterId = entry.arguments?.getString("letterId") ?: ""
                LetterMapScreen(
                    letterId = letterId,
                    onBack = { navController.popBackStack() },
                    vm = vm
                )
            }
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
