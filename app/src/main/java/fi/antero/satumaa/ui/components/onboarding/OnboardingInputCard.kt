package fi.antero.satumaa.ui.components.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import fi.antero.satumaa.R
import fi.antero.satumaa.ui.theme.OverlayScrim
import fi.antero.satumaa.ui.theme.StorybookPaper

/**
 * Kortti, joka pyytää käyttäjältä nimen.
 *
 * @param name Käyttäjän syöttämä nimi (tila).
 * @param onNameChange Callback nimen muutokselle.
 */
@Composable
fun OnboardingInputCard(
    name: String,
    onNameChange: (String) -> Unit
) {
    Surface(
        color = OverlayScrim, // Käytetään teeman puoliläpinäkyvää tummaa väriä
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Otsikko
            Text(
                text = stringResource(R.string.onboarding_title),
                style = MaterialTheme.typography.headlineLarge,
                color = StorybookPaper,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Alaotsikko
            Text(
                text = stringResource(R.string.onboarding_subtitle),
                style = MaterialTheme.typography.bodyLarge,
                color = StorybookPaper.copy(alpha = 0.9f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Tekstikenttä (Outlined on usein selkeämpi tummalla taustalla)
            OutlinedTextField(
                value = name,
                onValueChange = onNameChange,
                label = {
                    Text(
                        stringResource(R.string.onboarding_name_label),
                        color = StorybookPaper.copy(alpha = 0.7f)
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.Black.copy(alpha = 0.3f),
                    unfocusedContainerColor = Color.Black.copy(alpha = 0.2f),
                    focusedTextColor = StorybookPaper,
                    unfocusedTextColor = StorybookPaper,
                    cursorColor = StorybookPaper,
                    focusedBorderColor = StorybookPaper,
                    unfocusedBorderColor = StorybookPaper.copy(alpha = 0.5f)
                )
            )
        }
    }
}