package fi.antero.satumaa.ui.components.letter.flow

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import fi.antero.satumaa.R
import fi.antero.satumaa.ui.theme.StorybookPaper

/**
 * Näyttää lataustilan, kun odotetaan Joulupukin vastausta.
 */
@Composable
fun LetterStatusView() {
    Spacer(Modifier.height(24.dp))
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        CircularProgressIndicator(color = StorybookPaper)
        Spacer(Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.letter_status_thinking),
            style = MaterialTheme.typography.bodyLarge,
            color = StorybookPaper
        )
    }
}