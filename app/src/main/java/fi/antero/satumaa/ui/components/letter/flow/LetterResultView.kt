package fi.antero.satumaa.ui.components.letter.flow

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fi.antero.satumaa.R
import fi.antero.satumaa.ui.theme.Ink
import fi.antero.satumaa.ui.theme.OverlayScrim
import fi.antero.satumaa.ui.theme.StorybookPaper

/**
 * Näyttää vastauksen ja toiminnot.
 */
@Composable
fun LetterResultView(
    sentText: String,
    replyText: String?,
    status: String?,
    isOpened: Boolean,
    isViewMode: Boolean,
    onOpenAR: () -> Unit,
    onOpenMath: () -> Unit,
    onNewLetter: () -> Unit
) {
    // 1. Käyttäjän oma kirje
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(OverlayScrim, RoundedCornerShape(16.dp))
            .padding(20.dp)
    ) {
        Text(
            text = stringResource(R.string.letter_result_your_letter),
            style = MaterialTheme.typography.labelLarge,
            color = StorybookPaper.copy(alpha = 0.7f)
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = sentText,
            style = MaterialTheme.typography.bodyMedium.copy(fontStyle = FontStyle.Italic),
            color = StorybookPaper.copy(alpha = 0.8f)
        )
    }

    Spacer(Modifier.height(16.dp))

    // 2. Pukin vastaus (Avattu)
    if (isOpened) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(OverlayScrim.copy(alpha = 0.9f), RoundedCornerShape(16.dp))
                .padding(24.dp)
        ) {
            Text(
                text = stringResource(R.string.letter_result_santa_reply_title),
                style = MaterialTheme.typography.titleMedium,
                color = StorybookPaper
            )
            Spacer(Modifier.height(12.dp))

            if (replyText.isNullOrBlank()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = StorybookPaper,
                        strokeWidth = 2.dp
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(
                        stringResource(R.string.letter_result_opening),
                        color = StorybookPaper
                    )
                }
            } else {
                Text(
                    text = replyText,
                    style = MaterialTheme.typography.bodyLarge.copy(lineHeight = 24.sp),
                    color = StorybookPaper
                )
            }
        }
    }
    // 3. Vastaus saapunut, mutta EI avattu (Toiminnot)
    else if (!replyText.isNullOrEmpty() || status == "replied") {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(StorybookPaper.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                stringResource(R.string.letter_result_arrived_title),
                style = MaterialTheme.typography.displayLarge
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.letter_result_arrived_text),
                style = MaterialTheme.typography.titleMedium,
                color = StorybookPaper
            )
            Spacer(Modifier.height(32.dp))

            Button(
                onClick = onOpenAR,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = StorybookPaper,
                    contentColor = Ink
                )
            ) { Text(stringResource(R.string.letter_result_open_ar)) }

            Spacer(Modifier.height(12.dp))
            Text(
                text = stringResource(R.string.letter_result_or),
                color = StorybookPaper.copy(alpha = 0.9f),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(12.dp))

            Button(
                onClick = onOpenMath,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = StorybookPaper,
                    contentColor = Ink
                )
            ) { Text(stringResource(R.string.letter_result_open_math)) }
        }
    }
    // 4. Katselutila (odottaa vastausta)
    else if (isViewMode) {
        Text(
            text = stringResource(R.string.letter_result_reading),
            color = StorybookPaper,
            style = MaterialTheme.typography.bodyLarge
        )
    }

    Spacer(Modifier.height(24.dp))

    // 5. Uusi kirje -nappi (OutlinedButton käyttää StorybookPaperia)
    OutlinedButton(
        onClick = onNewLetter,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = StorybookPaper),
        border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(StorybookPaper))
    ) { Text(stringResource(R.string.letter_new_button)) }

    Spacer(Modifier.height(32.dp))
}