package fi.antero.satumaa.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.rememberNavController
import fi.antero.satumaa.ui.navigation.AppNavGraph
import fi.antero.satumaa.ui.navigation.RootRoute

@Composable
fun SatumaaApp(
    launchedLetterId: String? = null,
    onNavigationHandled: () -> Unit = {}
) {
    val navController = rememberNavController()

    // Tämä effecti ajetaan aina kun launchedLetterId muuttuu (ja ei ole null)
    LaunchedEffect(launchedLetterId) {
        if (launchedLetterId != null) {
            // Navigoidaan suoraan kirjeen karttanäkymään
            // Varmista, että reitti täsmää AppNavGraphisi määrittelyyn!
            // Esim. jos reitti on "letter_map/{letterId}", kutsu on näin:
            navController.navigate("${RootRoute.LetterMap.route}/$launchedLetterId")

            // Ilmoitetaan MainActivitylle, että navigaatio on hoidettu,
            // jotta se ei navigoi uudestaan esim. ruudun käännössä.
            onNavigationHandled()
        }
    }

    AppNavGraph(
        navController = navController,
        startDestination = RootRoute.Login.route
    )
}