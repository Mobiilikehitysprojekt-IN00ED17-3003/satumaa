package fi.antero.satumaa.ui.components.letter.flow

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import fi.antero.satumaa.R
import fi.antero.satumaa.ui.theme.Ink
import fi.antero.satumaa.ui.theme.OverlayScrim
import fi.antero.satumaa.ui.theme.StorybookPaper

/**
 * Näkymä uuden kirjeen kirjoittamiseen.
 *
 * @param userName Käyttäjän nimi.
 * @param text Kirjoitettu teksti.
 * @param isSending Onko lähetys käynnissä.
 * @param canGoMap Voiko karttanäkymään siirtyä.
 * @param onTextChange Callback tekstin muutokselle.
 * @param onSend Callback lähetysnapille.
 */
@Composable
fun LetterInputView(
    userName: String,
    text: String,
    isSending: Boolean,
    canGoMap: Boolean,
    onTextChange: (String) -> Unit,
    onSend: () -> Unit
) {
    // Otsikko-osio (tumma tausta luettavuuden parantamiseksi)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(OverlayScrim, RoundedCornerShape(16.dp))
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.letter_input_title),
            style = MaterialTheme.typography.headlineSmall,
            color = StorybookPaper
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.letter_input_greeting, userName),
            style = MaterialTheme.typography.bodyMedium,
            color = StorybookPaper.copy(alpha = 0.9f)
        )
    }

    Spacer(Modifier.height(24.dp))

    // Syöttökenttä ja toimintopainike
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(OverlayScrim, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        val maxChar = 200

        OutlinedTextField(
            value = text,
            onValueChange = { if (it.length <= maxChar) onTextChange(it) },
            label = {
                Text(
                    stringResource(R.string.letter_input_placeholder),
                    color = StorybookPaper.copy(0.7f)
                )
            },
            modifier = Modifier.fillMaxWidth(),
            minLines = 6,
            enabled = !isSending,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = OverlayScrim.copy(alpha = 0.3f),
                unfocusedContainerColor = OverlayScrim.copy(alpha = 0.3f),
                focusedTextColor = StorybookPaper,
                unfocusedTextColor = StorybookPaper,
                cursorColor = StorybookPaper,
                focusedBorderColor = StorybookPaper,
                unfocusedBorderColor = StorybookPaper.copy(alpha = 0.5f)
            )
        )

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = onSend,
            enabled = text.trim().isNotEmpty() && !isSending && canGoMap,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = StorybookPaper,
                contentColor = Ink
            )
        ) {
            val label = when {
                isSending -> stringResource(R.string.letter_input_sending)
                !canGoMap -> stringResource(R.string.letter_input_waiting_location)
                else -> stringResource(R.string.letter_input_send)
            }
            Text(label)
        }
    }
}