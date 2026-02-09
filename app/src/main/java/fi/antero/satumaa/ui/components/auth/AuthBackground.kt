package fi.antero.satumaa.ui.components.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import fi.antero.satumaa.ui.theme.LocalAppImages

/**
 * Kirjautumisnäkymän taustakuva.
 * Hakee kuvan dynaamisesti LocalAppImages-providerista.
 */
@Composable
fun AuthBackground() {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = LocalAppImages.current.authBackground),
            contentDescription = null, // Dekoratiivinen kuva
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}