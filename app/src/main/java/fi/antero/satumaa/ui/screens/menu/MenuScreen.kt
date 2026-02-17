package fi.antero.satumaa.ui.screens.menu

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState // <-- Lisätty import
import androidx.compose.foundation.verticalScroll // <-- Lisätty import
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import fi.antero.satumaa.R
import fi.antero.satumaa.ui.components.AppPageLayout
import fi.antero.satumaa.ui.components.AppTopBar
import fi.antero.satumaa.ui.components.menu.MenuHeader
import fi.antero.satumaa.ui.components.menu.MenuOptions
import fi.antero.satumaa.ui.navigation.RootRoute
import fi.antero.satumaa.ui.theme.AppDimensions
import fi.antero.satumaa.ui.theme.LocalAppImages

/**
 * Päävalikko (MenuScreen).
 *
 * Toimii sovelluksen keskuspaikkana kirjautumisen jälkeen.
 * Kokoaa yhteen otsikon ja navigaatiokortit.
 */
@Composable
fun MenuScreen(
    currentRoute: String?,
    userName: String = stringResource(R.string.menu_default_user),
    onNavigate: (String) -> Unit
) {
    // Alustetaan scrollauksen tila
    val scrollState = rememberScrollState()

    AppPageLayout(
        backgroundImageRes = LocalAppImages.current.menuBackground,
        topBar = {
            AppTopBar(
                onOpenProfile = { onNavigate(RootRoute.Profile.route) }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                // TÄMÄ MAHDOLLISTAA SCROLLAUKSEN VAAKATILASSA:
                .verticalScroll(scrollState)
                .padding(horizontal = AppDimensions.ScreenPadding, vertical = 40.dp),
            // Arrangement.Bottom on ok, mutta scrollatessa sisältö alkaa
            // yleensä ylhäältä jos tila loppuu.
            // Center tai Bottom toimii tässä, kunhan verticalScroll on päällä.
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. Tervehdys ja otsikko
            MenuHeader(userName = userName)

            // 2. Valikkokortit
            MenuOptions(
                onStoryClick = { onNavigate(RootRoute.Story.route) },
                onLetterClick = { onNavigate(RootRoute.Letter.route) }
            )

            Spacer(Modifier.height(AppDimensions.BottomPadding))
        }
    }
}