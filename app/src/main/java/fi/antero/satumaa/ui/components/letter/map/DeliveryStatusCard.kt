package fi.antero.satumaa.ui.components.letter.map

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import fi.antero.satumaa.R
import fi.antero.satumaa.ui.theme.Forest
import fi.antero.satumaa.ui.theme.StorybookPaper

/**
 * Kortti, joka näyttää kirjeen toimituksen edistymisen (progress bar) ja etäisyyden.
 * Kortti kelluu karttanäkymän alareunassa.
 *
 * @param progress Toimituksen edistyminen (0.0 - 1.0).
 * @param distanceKm Matka kilometreinä.
 * @param onBack Callback, kun käyttäjä haluaa palata (aktiivinen kun kirje on perillä).
 */
@Composable
fun DeliveryStatusCard(
    progress: Float,
    distanceKm: Float,
    onBack: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(24.dp)
            .fillMaxWidth(), // Varmistetaan, että kortti on leveä
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(
            // Teeman paperin väri pienellä läpinäkyvyydellä, jotta kartta kuultaa hieman läpi
            containerColor = StorybookPaper.copy(alpha = 0.95f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (progress < 1f) {
                // --- TILA: MATKALLA ---
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        stringResource(R.string.letter_map_on_way),
                        style = MaterialTheme.typography.labelLarge
                    )
                    Text(
                        text = "${(progress * 100).toInt()} %",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Forest // Teeman vihreä korostus
                    )
                }

                Spacer(Modifier.height(8.dp))

                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth(),
                    color = Forest,
                    trackColor = Forest.copy(alpha = 0.3f)
                )
            } else {
                // --- TILA: PERILLÄ ---
                Text(
                    text = stringResource(R.string.letter_map_arrived_title),
                    style = MaterialTheme.typography.titleMedium,
                    color = Forest,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(4.dp))
                // Muotoillaan etäisyys: %.1f tarkoittaa yhtä desimaalia
                Text(
                    text = stringResource(R.string.letter_map_arrived_desc, distanceKm),
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(Modifier.height(16.dp))

                Button(
                    onClick = onBack,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Forest)
                ) {
                    Text(stringResource(R.string.letter_map_back_button))
                }
            }
        }
    }
}