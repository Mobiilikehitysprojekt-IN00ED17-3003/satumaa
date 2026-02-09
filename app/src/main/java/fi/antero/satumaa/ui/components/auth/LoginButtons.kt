package fi.antero.satumaa.ui.components.auth

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import fi.antero.satumaa.R
import fi.antero.satumaa.ui.theme.Forest
import fi.antero.satumaa.ui.theme.StorybookPaper

/**
 * Sisältää sovelluksen kirjautumispainikkeet.
 *
 * @param onGoogleClick Callback Google-kirjautumiselle.
 * @param onAnonymousClick Callback anonyymille kokeilulle.
 */
@Composable
fun LoginButtons(
    onGoogleClick: () -> Unit,
    onAnonymousClick: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {

        // Pääpainike: Google Login (Vihreä Forest-väri)
        Button(
            onClick = onGoogleClick,
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .padding(bottom = 12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Forest,
                contentColor = StorybookPaper
            )
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_google),
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(stringResource(R.string.auth_login_google))
        }

        // Toissijainen painike: Kokeile ilman tiliä (Tekstipainike)
        TextButton(onClick = onAnonymousClick) {
            Text(
                text = stringResource(R.string.auth_login_anonymous),
                color = StorybookPaper.copy(alpha = 0.8f),
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}