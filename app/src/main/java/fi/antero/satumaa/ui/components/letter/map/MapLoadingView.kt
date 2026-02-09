package fi.antero.satumaa.ui.components.letter.map

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import fi.antero.satumaa.R
import fi.antero.satumaa.ui.theme.StorybookPaper

/**
 * Näkymä, jota näytetään, kun käyttäjän sijaintia haetaan.
 * Taustalla on kuva Joulupukin kylästä, mikä luo tunnelmaa odotukseen.
 */
@Composable
fun MapLoadingView() {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.joulupukin_kyla),
            contentDescription = null, // Dekoratiivinen tausta
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Käytetään StorybookPaper-väriä tumman taustakuvan päällä
            CircularProgressIndicator(color = StorybookPaper)
            Spacer(Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.letter_map_searching_loc),
                style = MaterialTheme.typography.bodyLarge,
                color = StorybookPaper
            )
        }
    }
}