package fi.antero.satumaa.ui.screens.letter

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import fi.antero.satumaa.R
import fi.antero.satumaa.data.model.Letter
import fi.antero.satumaa.ui.components.AppPageLayout
import fi.antero.satumaa.ui.components.AppTopBar
import fi.antero.satumaa.ui.components.letter.list.DeleteLetterDialog
import fi.antero.satumaa.ui.components.letter.list.EmptyMailboxView
import fi.antero.satumaa.ui.components.letter.list.LetterCard
import fi.antero.satumaa.ui.components.letter.list.LetterListBackground // Tuodaan oma taustakomponentti
import fi.antero.satumaa.ui.viewmodel.letter.LetterListViewModel

/**
 * LetterListScreen on päänäkymä, joka listaa kaikki käyttäjän lähettämät ja vastaanottamat kirjeet.
 *
 * Toiminnallisuudet:
 * 1. Hakee datan ViewModelista (Room/Firestore).
 * 2. Mahdollistaa listan päivityksen "vedä alas" -eleellä (Pull-to-refresh).
 * 3. Hallinnoi kirjeen poistamista ja siihen liittyvää varmistusdialogia.
 * 4. Käyttää kustomoitua taustakomponenttia (LetterListBackground).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LetterListScreen(
    onNavigateBack: () -> Unit,
    onLetterClick: (String) -> Unit,
    viewModel: LetterListViewModel = hiltViewModel()
) {
    // Kerätään kirjeet ViewModelin Flowsta UI-tilaksi.
    val letters by viewModel.letters.collectAsState()

    // Paikalliset tilamuuttujat:
    // isRefreshing: Kontrolloi latausindikaattorin näkyvyyttä päivityksen aikana.
    var isRefreshing by remember { mutableStateOf(false) }
    // letterToDelete: Pitää muistissa kirjeen, jonka käyttäjä haluaa poistaa.
    // Jos tämä ei ole null, poistodialogi näytetään ruudulla.
    var letterToDelete by remember { mutableStateOf<Letter?>(null) }

    // Käytetään sovelluksen yhtenäistä sivupohjaa.
    // HUOM: Käytämme tässä 'background'-parametria (eikä resurssi-ID:tä), jotta voimme syöttää
    // oman LetterListBackground-komponenttimme. Tämä mahdollistaa Joulupukin kylä -taustan käytön.
    AppPageLayout(
        background = { LetterListBackground() },
        topBar = {
            AppTopBar(
                overrideTitle = stringResource(R.string.letter_list_title),
                showBack = true,
                onBack = onNavigateBack
            )
        }
    ) { padding ->

        // PullToRefreshBox käärii listan sisäänsä ja mahdollistaa päivityksen vetämällä.
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = {
                isRefreshing = true
                viewModel.refreshLetters()
                // Lisätään pieni viive (1s) UI-kokemuksen parantamiseksi, jotta latausindikaattori
                // ehtii vilahtaa näkyvissä, vaikka data latautuisi välimuistista heti.
                android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                    isRefreshing = false
                }, 1000)
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (letters.isEmpty()) {
                // Jos lista on tyhjä, näytetään informatiivinen näkymä.
                EmptyMailboxView()
            } else {
                // Listataan kirjeet LazyColumnilla, joka on tehokas pitkille listoille.
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Käytetään 'key'-parametria (it.id), jotta Compose osaa optimoida päivitykset
                    // ja tunnistaa, jos listan järjestys muuttuu.
                    items(letters, key = { it.id }) { letter ->
                        LetterCard(
                            letter = letter,
                            onClick = { onLetterClick(letter.id) },
                            onDelete = { letterToDelete = letter } // Asetetaan poistettava kirje tilaan -> avaa dialogin
                        )
                    }
                }
            }
        }

        // Näytetään poistodialogi ehdollisesti (vain jos käyttäjä on painanut roskakoria).
        // Tämä on "Overlay"-tyyppinen komponentti, joka piirtyy muun sisällön päälle.
        if (letterToDelete != null) {
            DeleteLetterDialog(
                onConfirm = {
                    letterToDelete?.let { viewModel.deleteLetter(it.id) }
                    letterToDelete = null // Nollataan valinta, jolloin dialogi sulkeutuu
                },
                onDismiss = { letterToDelete = null } // Suljetaan dialogi ilman toimenpiteitä
            )
        }
    }
}