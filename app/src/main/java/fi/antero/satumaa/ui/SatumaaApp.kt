package fi.antero.satumaa.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.rememberNavController
import fi.antero.satumaa.ui.navigation.AppNavGraph
import fi.antero.satumaa.ui.navigation.RootRoute

/**
 * Sovelluksen ylätason Composable-funktio.
 *
 * Tämä komponentti:
 * 1. Luo NavControllerin (joka hallinnoi siirtymiä ruutujen välillä).
 * 2. Käsittelee "Deep Linkit" eli tässä tapauksessa ilmoituksesta saapumisen.
 * 3. Sisältää varsinaisen navigaatiograafin (AppNavGraph).
 *
 * @param launchedLetterId Jos sovellus avattiin ilmoituksesta, tässä on kirjeen ID.
 * @param onNavigationHandled Callback, jota kutsutaan kun navigointi on suoritettu (nollaa tilan MainActivityssä).
 */
@Composable
fun SatumaaApp(
    launchedLetterId: String? = null,
    onNavigationHandled: () -> Unit = {}
) {
    val navController = rememberNavController()

    // Reagoidaan ilmoituksesta saapumiseen.
    // LaunchedEffect ajetaan aina, kun 'launchedLetterId' muuttuu (eikä ole null).
    LaunchedEffect(launchedLetterId) {
        if (launchedLetterId != null) {
            // Navigoidaan suoraan kirjeen karttanäkymään (tai lukunäkymään).
            // Tässä oletetaan, että reitti on määritelty AppNavGraphissa muodossa "letter_map/{letterId}"
            navController.navigate("${RootRoute.LetterMap.route}/$launchedLetterId")

            // Tärkeää: Ilmoitetaan MainActivitylle, että navigaatio on hoidettu.
            // Tämä estää sen, että ruudun kääntäminen tai muu recomposition
            // navigoisi sinne vahingossa uudestaan.
            onNavigationHandled()
        }
    }

    // Piirretään sovelluksen navigaatiorakenne
    AppNavGraph(
        navController = navController,
        // Sovellus alkaa aina kirjautumisruudusta (LoginScreen tarkistaa onko käyttäjä jo sisällä)
        startDestination = RootRoute.Login.route
    )
}