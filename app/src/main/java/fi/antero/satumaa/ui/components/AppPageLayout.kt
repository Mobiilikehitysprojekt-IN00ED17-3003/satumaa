package fi.antero.satumaa.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource

/**
 * Sovelluksen perusulkoasu (Layout).
 *
 * @param backgroundImageRes (Valinnainen) Taustakuvan resurssi-ID. Käytetään oletustaustana, jos 'background' on tyhjä.
 * @param background (Valinnainen) Kustomoitu taustakomponentti (esim. LetterListBackground). Ohittaa backgroundImageRes:n.
 * @param topBar Yläpalkki.
 * @param bottomBar Alapalkki.
 * @param content Varsinainen sisältö.
 */
@Composable
fun AppPageLayout(
    backgroundImageRes: Int? = null, // Muutettu nullableksi
    background: @Composable () -> Unit = {
        // Oletustoteutus: Jos resurssi-ID on annettu, piirretään kuva.
        if (backgroundImageRes != null) {
            Image(
                painter = painterResource(id = backgroundImageRes),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
    },
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        topBar = topBar,
        bottomBar = bottomBar
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {

            // 1. Piirretään tausta (joko kuva tai kustomoitu komponentti)
            background()

            // 2. Piirretään sisältö
            content(padding)
        }
    }
}