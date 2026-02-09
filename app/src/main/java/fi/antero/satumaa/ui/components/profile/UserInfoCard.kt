package fi.antero.satumaa.ui.components.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import fi.antero.satumaa.R
import fi.antero.satumaa.ui.theme.Forest
import fi.antero.satumaa.ui.theme.Ink
import fi.antero.satumaa.ui.theme.StorybookPaper
import fi.antero.satumaa.ui.theme.Terracotta

/**
 * Kortti, joka näyttää kirjautuneen käyttäjän tiedot ja uloskirjautumispainikkeen.
 *
 * @param email Käyttäjän sähköpostiosoite.
 * @param onLogout Callback, kun käyttäjä painaa "Kirjaudu ulos".
 */
@Composable
fun UserInfoCard(
    email: String,
    onLogout: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            // Käytetään StorybookPaperia pienellä läpinäkyvyydellä
            containerColor = StorybookPaper.copy(alpha = 0.9f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Otsikko: "Kirjautunut käyttäjä:"
            Text(
                text = stringResource(R.string.profile_logged_in_as),
                style = MaterialTheme.typography.titleMedium,
                color = Ink
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Sähköposti (Korostetaan Forest-vihreällä)
            Text(
                text = email,
                style = MaterialTheme.typography.bodyLarge,
                color = Forest
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Uloskirjautuminen (Terracotta-punainen huomioväri)
            Button(
                onClick = onLogout,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Terracotta,
                    contentColor = StorybookPaper
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.profile_logout))
            }
        }
    }
}