package fi.antero.satumaa.ui.screens.menu

import androidx.compose.foundation.layout.*
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
    // Käytetään AppPageLayoutia, koska se hallinnoi kätevästi TopBarin ja taustakuvan
    // (LocalAppImages.current.menuBackground) yhteistoiminnan.
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
                .padding(horizontal = AppDimensions.ScreenPadding, vertical = 40.dp),
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