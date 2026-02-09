package fi.antero.satumaa.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import fi.antero.satumaa.R
import fi.antero.satumaa.ui.theme.StorybookPaper

/**
 * Yleiskäyttöinen virhenäkymä.
 *
 * Näyttää virheilmoituksen tyylitellyssä kortissa ja tarjoaa "Yritä uudelleen" -painikkeen.
 * Käytetään esimerkiksi verkkoyhteysongelmien tai latausvirheiden ilmoittamiseen.
 *
 * @param message Näytettävä virheviesti.
 * @param onRetry Callback-funktio, kun käyttäjä painaa "Yritä uudelleen".
 * @param modifier Komponentin muokkain.
 */
@Composable
fun ErrorView(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            // Käytetään tummaa, hieman läpinäkyvää taustaa virheilmoitukselle,
            // jotta se erottuu selkeästi muusta sisällöstä.
            containerColor = Color.Black.copy(alpha = 0.75f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = MaterialTheme.shapes.large
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Otsikko ("Hupsista!")
            Text(
                text = stringResource(R.string.error_title_oops),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.error, // Punainen huomioväri
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(12.dp))

            // Varsinainen virheviesti
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = StorybookPaper, // Vaalea teksti tummalla pohjalla luettavuuden vuoksi
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(24.dp))

            // Toimintopainike
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                )
            ) {
                Text(stringResource(R.string.error_retry_button))
            }
        }
    }
}