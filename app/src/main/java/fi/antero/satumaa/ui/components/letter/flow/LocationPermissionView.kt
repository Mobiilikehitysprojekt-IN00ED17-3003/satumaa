package fi.antero.satumaa.ui.components.letter.flow

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import fi.antero.satumaa.R
import fi.antero.satumaa.ui.theme.Ink
import fi.antero.satumaa.ui.theme.OverlayScrim
import fi.antero.satumaa.ui.theme.StorybookPaper

@Composable
fun LocationPermissionView(
    onRequestPermission: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(OverlayScrim, RoundedCornerShape(16.dp))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.letter_perm_title),
            color = StorybookPaper,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.letter_perm_desc),
            color = StorybookPaper.copy(alpha = 0.9f)
        )
        Spacer(Modifier.height(12.dp))
        Button(
            onClick = onRequestPermission,
            colors = ButtonDefaults.buttonColors(
                containerColor = StorybookPaper,
                contentColor = Ink
            ),
            modifier = Modifier.fillMaxWidth()
        ) { Text(stringResource(R.string.letter_perm_button)) }
    }
}

@Composable
fun LocationLoadingView() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(OverlayScrim, RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(20.dp),
            color = StorybookPaper,
            strokeWidth = 2.dp
        )
        Spacer(Modifier.width(12.dp))
        Text(
            stringResource(R.string.letter_loc_searching),
            color = StorybookPaper
        )
    }
}