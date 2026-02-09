package fi.antero.satumaa.ui.components.auth

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import fi.antero.satumaa.R
import fi.antero.satumaa.ui.theme.StorybookPaper

/**
 * Kirjautumisnäkymän otsikko.
 * Käyttää StorybookPaper-väriä erottuakseen tummasta taustasta.
 */
@Composable
fun AuthHeader() {
    Text(
        text = stringResource(R.string.auth_welcome_title),
        style = MaterialTheme.typography.headlineMedium,
        color = StorybookPaper
    )
}