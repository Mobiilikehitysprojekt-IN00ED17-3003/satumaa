package fi.antero.satumaa.ui.components.story.create

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fi.antero.satumaa.R
import fi.antero.satumaa.data.model.Story
import fi.antero.satumaa.ui.theme.Forest
import fi.antero.satumaa.ui.theme.StorybookPaper

/**
 * Näkymä, joka esittää valmiin sadun ja tarjoaa toiminnot sen tallentamiseen tai hylkäämiseen.
 *
 * @param story Näytettävä satu.
 * @param isSaved Onko satu jo tallennettu (tietokannassa).
 * @param onSave Callback tallennukselle.
 * @param onDiscard Callback hylkäykselle (tai "Tee uusi" -toiminnolle).
 */
@Composable
fun StoryResultView(
    story: Story,
    isSaved: Boolean, // Jos id löytyy, se on tallennettu
    onSave: () -> Unit,
    onDiscard: () -> Unit
) {
    Column(
        modifier = Modifier
            .background(Color.Black.copy(0.6f), RoundedCornerShape(16.dp))
            .padding(24.dp)
    ) {
        // Otsikko
        Text(
            text = story.title,
            style = MaterialTheme.typography.headlineMedium,
            color = StorybookPaper,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
            fontWeight = FontWeight.Bold
        )

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 16.dp),
            color = StorybookPaper.copy(0.5f)
        )

        // Sisältö
        Text(
            text = story.content,
            style = MaterialTheme.typography.bodyLarge.copy(lineHeight = 32.sp),
            color = StorybookPaper
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Toimintopainikkeet
        if (!isSaved) {
            // Satu on esikatselussa (Preview)
            Button(
                onClick = onSave,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Forest,
                    contentColor = Color.White
                )
            ) {
                Icon(Icons.Default.AutoAwesome, null)
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.story_result_save_button))
            }

            Spacer(Modifier.height(12.dp))

            OutlinedButton(
                onClick = onDiscard,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = StorybookPaper)
            ) {
                Text(stringResource(R.string.story_result_discard_button))
            }
        } else {
            // Satu on tallennettu tai ladattu kirjastosta
            Text(
                text = stringResource(R.string.story_result_saved_text),
                color = Forest,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(Modifier.height(16.dp))

            Button(
                onClick = onDiscard, // Toimii "Tee uusi" -nappina tässä tilassa
                modifier = Modifier.align(Alignment.CenterHorizontally),
                colors = ButtonDefaults.buttonColors(
                    containerColor = StorybookPaper.copy(alpha = 0.2f),
                    contentColor = StorybookPaper
                )
            ) {
                Text(stringResource(R.string.story_result_new_button))
            }
        }
    }
}