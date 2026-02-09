package fi.antero.satumaa.ui.components.letter.flow

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fi.antero.satumaa.R
import fi.antero.satumaa.ui.theme.Forest
import fi.antero.satumaa.ui.theme.Ink
import fi.antero.satumaa.ui.theme.StorybookPaper
import fi.antero.satumaa.util.MathChallenge

@Composable
fun MathChallengeDialog(
    challenge: MathChallenge,
    isError: Boolean,
    onDismiss: () -> Unit,
    onSubmit: (String) -> Unit
) {
    var answer by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = StorybookPaper,
        titleContentColor = Ink,
        textContentColor = Ink,
        title = {
            Text(
                stringResource(R.string.letter_math_title),
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                Text(
                    stringResource(R.string.letter_math_desc),
                    fontSize = 16.sp
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    text = challenge.question,
                    style = MaterialTheme.typography.displaySmall,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    fontWeight = FontWeight.Bold,
                    color = Forest
                )
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(
                    value = answer,
                    onValueChange = { if (it.length <= 3 && it.all { c -> c.isDigit() }) answer = it },
                    label = { Text(stringResource(R.string.letter_math_answer_label)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = isError,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Forest,
                        focusedLabelColor = Forest,
                        cursorColor = Forest,
                        errorCursorColor = MaterialTheme.colorScheme.error
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSubmit(answer) },
                enabled = answer.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Forest,
                    contentColor = Color.White
                )
            ) { Text(stringResource(R.string.letter_math_open_button)) }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(contentColor = Ink)
            ) {
                Text(stringResource(R.string.letter_math_cancel))
            }
        }
    )
}