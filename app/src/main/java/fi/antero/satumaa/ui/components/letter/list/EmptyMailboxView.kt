package fi.antero.satumaa.ui.components.letter.list

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import fi.antero.satumaa.R
import fi.antero.satumaa.ui.theme.OverlayScrim
import fi.antero.satumaa.ui.theme.StorybookPaper

/**
 * Näytetään käyttäjälle, kun postilaatikossa ei ole yhtään kirjettä.
 * Käyttää tummaa taustaa (OverlayScrim) erottuakseen taustakuvasta.
 */
@Composable
fun EmptyMailboxView() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            color = OverlayScrim, // Puoliläpinäkyvä tumma tausta
            shape = MaterialTheme.shapes.medium
        ) {
            Text(
                text = stringResource(R.string.letter_list_empty),
                color = StorybookPaper,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}