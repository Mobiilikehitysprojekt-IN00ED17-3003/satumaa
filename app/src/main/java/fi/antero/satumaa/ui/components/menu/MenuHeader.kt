package fi.antero.satumaa.ui.components.menu

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fi.antero.satumaa.R
import fi.antero.satumaa.ui.theme.OverlayScrim
import fi.antero.satumaa.ui.theme.StorybookPaper

/**
 * Valikon yläosa: Tervehdys ja alaotsikko.
 *
 * @param userName Käyttäjän nimi, joka näytetään tervehdyksessä.
 */
@Composable
fun MenuHeader(userName: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Tervehdys (esim. "Terve, Seikkailija!")
        Text(
            text = stringResource(R.string.menu_greeting, userName),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp,
                // Varjo parantaa luettavuutta taustakuvan päällä
                shadow = Shadow(
                    color = OverlayScrim,
                    offset = Offset(2f, 4f),
                    blurRadius = 8f
                )
            ),
            color = StorybookPaper
        )

        // Alaotsikko ("Mitä taikaa tänään luodaan?")
        Text(
            text = stringResource(R.string.menu_subtitle),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge.copy(
                shadow = Shadow(
                    color = OverlayScrim,
                    offset = Offset(1f, 2f),
                    blurRadius = 4f
                )
            ),
            color = StorybookPaper.copy(alpha = 0.9f)
        )
    }
}