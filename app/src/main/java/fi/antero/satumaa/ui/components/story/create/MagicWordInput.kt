package fi.antero.satumaa.ui.components.story.create

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import fi.antero.satumaa.ui.theme.StorybookPaper

/**
 * Tekstikenttä yhden taikasanan syöttämiseen.
 *
 * @param value Syötetty teksti.
 * @param onValueChange Callback tekstin muutokselle.
 * @param label Kentän otsikko (esim. "1. Taikasana").
 * @param imeAction Näppäimistön toiminto (Next/Done).
 * @param onDone Callback, kun Done-painiketta painetaan.
 */
@Composable
fun MagicWordInput(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    imeAction: ImeAction,
    onDone: () -> Unit = {}
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = StorybookPaper.copy(0.8f)) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = imeAction),
        keyboardActions = KeyboardActions(onDone = { onDone() }),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = StorybookPaper,
            unfocusedTextColor = StorybookPaper,
            focusedBorderColor = StorybookPaper,
            unfocusedBorderColor = StorybookPaper.copy(0.5f)
        )
    )
}