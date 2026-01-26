package fi.antero.satumaa.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import fi.antero.satumaa.ui.screens.letter.LetterCameraScreen
import fi.antero.satumaa.ui.screens.letter.LetterFlowScreen

@Composable
fun AppNavGraph(
    navController: NavHostController,
    startDestination: String = RootRoute.Letter.route,
    userName: String = "Seikkailija"
) {
    val backStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry.value?.destination?.route

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {

        // --- LETTER FLOW ---
        composable(RootRoute.Letter.route) {
            LetterFlowScreen(
                currentRoute = currentRoute,
                onNavigate = { route ->
                    navController.navigate(route)
                },
                userName = userName
            )
        }

        // --- LETTER CAMERA (AR) ---
        composable(LetterRoutes.CAMERA) {
            LetterCameraScreen(
                onFoundLetter = {
                    // Palataan takaisin kirjeeseen
                    navController.popBackStack()
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        // Huom: Jos käytät tätä tiedostoa, muista lisätä myös uusi LetterMapScreen tänne!
        // composable(RootRoute.LetterMap.route) {
        //     LetterMapScreen(onBack = { navController.popBackStack() })
        // }
    }
}