package fi.antero.satumaa.ui.components.letter.list

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import fi.antero.satumaa.R
import fi.antero.satumaa.ui.theme.Ink
import fi.antero.satumaa.ui.theme.StorybookPaper

/**
 * Varmistusdialogi, joka kysytään ennen kirjeen poistamista.
 *
 * @param onConfirm Callback, joka suorittaa varsinaisen poiston.
 * @param onDismiss Callback, joka sulkee dialogin ilman toimenpiteitä.
 */
@Composable
fun DeleteLetterDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = StorybookPaper,
        titleContentColor = Ink,
        textContentColor = Ink,
        title = { Text(stringResource(R.string.letter_list_delete_title)) },
        text = { Text(stringResource(R.string.letter_list_delete_confirm)) },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
            ) {
                Text(stringResource(R.string.letter_list_delete_action))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(contentColor = Ink)
            ) {
                Text(stringResource(R.string.letter_list_cancel_action))
            }
        }
    )
}