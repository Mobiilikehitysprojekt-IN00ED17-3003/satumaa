package fi.antero.satumaa.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import fi.antero.satumaa.ui.navigation.AppNavGraph
import fi.antero.satumaa.ui.navigation.RootRoute

@Composable
fun SatumaaApp() {
    val navController = rememberNavController()


    AppNavGraph(
        navController = navController,
        startDestination = RootRoute.Login.route
    )
}