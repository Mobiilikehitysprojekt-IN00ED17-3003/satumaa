package fi.antero.satumaa.ui.components.story.list

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import fi.antero.satumaa.R
import fi.antero.satumaa.data.model.Story
import fi.antero.satumaa.ui.theme.Ink
import fi.antero.satumaa.ui.theme.StorybookPaper

/**
 * Dialogi sadun poiston vahvistamiseen.
 *
 * @param story Poistettava satu.
 * @param onConfirm Callback poistolle.
 * @param onDismiss Callback peruutukselle.
 */
@Composable
fun DeleteStoryDialog(
    story: Story,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = StorybookPaper,
        titleContentColor = Ink,
        textContentColor = Ink,
        title = { Text(stringResource(R.string.story_list_delete_title)) },
        text = {
            // String formatoidaan sadun nimellä
            Text(stringResource(R.string.story_list_delete_confirm, story.title))
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
            ) {
                Text(stringResource(R.string.story_list_delete_action))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(contentColor = Ink)
            ) {
                Text(stringResource(R.string.story_list_cancel_action))
            }
        }
    )
}