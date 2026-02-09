package fi.antero.satumaa.ui.screens.profile.math.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import co.yml.charts.common.model.PlotType
import co.yml.charts.ui.piechart.charts.DonutPieChart
import co.yml.charts.ui.piechart.models.PieChartConfig
import co.yml.charts.ui.piechart.models.PieChartData
import fi.antero.satumaa.R
import fi.antero.satumaa.ui.viewmodel.stats.StatsUiState

/**
 * Donitsi-piirakkakaavio avainsanojen jakaumalle.
 *
 * Käyttää YCharts-kirjastoa. Näyttää keskellä valitun kategorian tiedot.
 */
@Composable
fun KeywordsPieChart(uiState: StatsUiState) {
    if (uiState.topKeywords.isEmpty()) return

    // Oletustekstit ("Tyylit", "Klikkaa sektoria")
    val defaultCenterText = stringResource(R.string.chart_pie_center_default)
    val defaultHintText = stringResource(R.string.chart_pie_click_hint)

    // Tila keskellä näytettävälle tekstille
    var selectedWord by remember { mutableStateOf(defaultCenterText) }
    var selectedCount by remember { mutableStateOf(defaultHintText) }

    // Formatointistringi luvuille (esim. "%1$d kpl (%2$d%%)")
    val countFormat = stringResource(R.string.chart_pie_count_format)

    // Muunnetaan StatsUiState-data YCharts-kirjaston ymmärtämään muotoon
    val slices = uiState.topKeywords.map { stat ->
        PieChartData.Slice(
            label = stat.styleName,
            value = stat.percentage,
            color = stat.color
        )
    }

    val pieChartData = PieChartData(
        slices = slices,
        plotType = PlotType.Donut // Donitsi-tyyli (reikä keskellä)
    )

    // Kaavion asetukset
    val pieChartConfig = PieChartConfig(
        isAnimationEnable = true,
        showSliceLabels = false, // Piilotetaan labelit itse graafista (ahtauden välttämiseksi)
        activeSliceAlpha = 0.8f, // Valitun sektorin korostus
        backgroundColor = Color.Transparent,
        isSumVisible = false,
        strokeWidth = 100f, // Renkaan paksuus
        chartPadding = 25,
        isClickOnSliceEnabled = true,
        labelColor = Color(0xFF1B1B1F)
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            contentAlignment = Alignment.Center
        ) {
            // YCharts Donitsi
            DonutPieChart(
                modifier = Modifier.fillMaxSize(),
                pieChartData = pieChartData,
                pieChartConfig = pieChartConfig,
                onSliceClick = { slice ->
                    // Päivitetään keskiteksti, kun käyttäjä klikkaa sektoria
                    selectedWord = slice.label

                    // Etsitään alkuperäinen data, jotta saadaan tarkka lukumäärä
                    val originalStat = uiState.topKeywords.find { it.styleName == slice.label }
                    val count = originalStat?.count ?: 0

                    selectedCount = String.format(countFormat, count, slice.value.toInt())
                }
            )

            // Teksti donitsin reiän sisällä
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = selectedWord,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1B1B1F)
                )
                if (selectedCount.isNotEmpty()) {
                    Text(
                        text = selectedCount,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.DarkGray
                    )
                }
            }
        }
    }
}