package fi.antero.satumaa.ui.screens.profile.math

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import fi.antero.satumaa.R
import fi.antero.satumaa.ui.screens.profile.math.components.*
import fi.antero.satumaa.ui.viewmodel.stats.StatsViewModel

/**
 * TÄMÄ ON PÄÄNÄKYMÄ (Container).
 *
 * Tämä Composable kokoaa kaikki erilliset graafikomponentit yhdeksi
 * skrollattavaksi listaksi. Se toimii "liimana" ViewModelin ja UI-komponenttien välillä.
 */
@Composable
fun ProfileMathSection(
    // Hilt syöttää ViewModelin automaattisesti tähän.
    viewModel: StatsViewModel = hiltViewModel()
) {
    // 1. TILAN KUUNTELU (State Collection)
    // Kerätään data ViewModelista. 'collectAsState' varmistaa, että kun
    // laskenta valmistuu (isLoading muuttuu falseksi), tämä ruutu piirretään uudelleen.
    val uiState by viewModel.uiState.collectAsState()
    val timeRange by viewModel.timeRange.collectAsState()

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(24.dp), // Välit korttien välillä
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // --- OTSIKKO ---
        // Otsikko varjolla (Valkoinen teksti, musta varjo takaa näkyvyyden kaikilla taustoilla)
        Text(
            text = stringResource(R.string.math_title_main), // "Satumainen Matematiikka"
            style = MaterialTheme.typography.headlineMedium.copy(
                shadow = Shadow(
                    color = Color.Black.copy(alpha = 0.5f),
                    offset = Offset(2f, 2f),
                    blurRadius = 4f
                )
            ),
            color = Color(0xFFFFF3E6), // StorybookPaper
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        // --- TILAN HALLINTA (Loading / Empty / Content) ---

        if (uiState.isLoading) {
            // TILA A: Lasketaan vielä...
            Text(
                stringResource(R.string.math_status_loading),
                color = Color(0xFFFFF3E6),
                style = MaterialTheme.typography.bodyLarge
            )
        } else if (uiState.totalStories == 0) {
            // TILA B: Ei yhtään satua tietokannassa
            Text(
                stringResource(R.string.math_status_empty),
                color = Color(0xFFFFF3E6),
                textAlign = TextAlign.Center
            )
        } else {
            // TILA C: Data on valmista -> Piirretään kortit

            // ---------------------------------------------------------
            // KORTTI 1: AKTIIVISUUS (WeeklyActivityChart)
            // ---------------------------------------------------------
            MathCard(
                title = stringResource(R.string.math_card_activity_title), // "Luomisaktiivisuus"
                description = stringResource(R.string.math_card_activity_desc) // "Kuinka monta satua..."
            ) {
                // Aikajänteen valitsin (Viikko / Kuukausi)
                // Kun tätä painetaan, kutsumme ViewModelia -> Data lasketaan uudelleen -> UI päivittyy
                TimeRangeSelector(
                    currentRange = timeRange,
                    onRangeSelected = { viewModel.setTimeRange(it) }
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Itse Pylväsdiagrammi-komponentti
                WeeklyActivityChart(uiState = uiState)
            }

            // ---------------------------------------------------------
            // KORTTI 2: MIELTYMYKSET (KeywordsPieChart)
            // ---------------------------------------------------------
            MathCard(
                title = stringResource(R.string.math_card_keywords_title), // "Suosituimmat aiheet"
                description = stringResource(R.string.math_card_keywords_desc)
            ) {
                // Itse Donitsikaavio-komponentti
                KeywordsPieChart(uiState = uiState)
            }

            // ---------------------------------------------------------
            // KORTTI 3: KEHITYS (TrendCombinedChart)
            // ---------------------------------------------------------
            MathCard(
                title = stringResource(R.string.math_card_trend_title), // "Tarinoiden pituus"
                description = stringResource(R.string.math_card_trend_desc)
            ) {
                // Pylväät + Punainen trendiviiva
                TrendCombinedChart(uiState = uiState)

                Spacer(modifier = Modifier.height(12.dp))
                // Pieni seliteteksti graafin alle
                Text(
                    text = stringResource(R.string.math_trend_legend),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // ---------------------------------------------------------
            // KORTTI 4: SEIKKAILUMITTARI (AdventureScatterChart)
            // ---------------------------------------------------------
            MathCard(
                title = stringResource(R.string.math_card_adventure_title), // "Seikkailuindeksi"
                description = stringResource(R.string.math_card_adventure_desc)
            ) {
                // Ohjeteksti ylhäällä
                Text(
                    text = stringResource(R.string.math_adventure_tooltip),
                    style = MaterialTheme.typography.labelMedium,
                    color = Color(0xFF2E6B5B),
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Tietolaatikko, joka selittää miten pisteet lasketaan
                Surface(
                    color = Color.Black.copy(alpha = 0.05f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = stringResource(R.string.math_adventure_info_title), // "Miten tämä toimii?"
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1B1B1F)
                        )
                        Text(
                            text = stringResource(R.string.math_adventure_info_desc),
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF2A2A31),
                            lineHeight = 16.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Akselien selitteet
                Text(
                    text = stringResource(R.string.math_adventure_axis_legend), // "X = Pituus, Y = Jännitys"
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Itse Hajakuvaaja (Scatter Plot)
                Box(modifier = Modifier.fillMaxWidth().height(320.dp)) {
                    AdventureScatterChart(uiState = uiState)
                }
            }
        }
    }
}

/**
 * APUKOMPONENTTI: Tyylitelty kortti
 *
 * Tämä pitää pääkoodin siistinä. Sen sijaan että määrittelisimme Cardin
 * värit ja varjot 4 kertaa uudestaan, teemme sen tässä kerran.
 *
 * @param content Lambda-funktio, johon itse graafi sijoitetaan ("Slot API")
 */
@Composable
fun MathCard(
    title: String,
    description: String? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFF3E6).copy(alpha = 0.9f) // Hieman läpinäkyvä "paperi"
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Kortin otsikko
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1B1B1F),
                textAlign = TextAlign.Center
            )

            // Valinnainen kuvausteksti
            if (description != null) {
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.DarkGray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Tähän renderöidään graafi, joka annettiin parametrina
            content()
        }
    }
}