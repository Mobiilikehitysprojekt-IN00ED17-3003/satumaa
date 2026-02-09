package fi.antero.satumaa.ui.screens.profile.math.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import co.yml.charts.axis.AxisData
import co.yml.charts.common.model.Point
import co.yml.charts.ui.barchart.BarChart
import co.yml.charts.ui.barchart.models.BarChartData
import co.yml.charts.ui.barchart.models.BarData
import co.yml.charts.ui.barchart.models.BarStyle
import fi.antero.satumaa.R
import fi.antero.satumaa.ui.viewmodel.stats.StatsUiState
import kotlin.math.max

/**
 * Piirtää pylväsdiagrammin viikoittaisesta aktiivisuudesta.
 *
 * Käyttää YCharts-kirjastoa (`co.yml.charts`).
 * X-akseli: Viikot (tai kuukaudet)
 * Y-akseli: Luotujen satujen määrä
 */
@Composable
fun WeeklyActivityChart(uiState: StatsUiState) {
    if (uiState.weeklyStats.isEmpty()) return

    val scrollState = rememberScrollState()
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val fixedTextColor = Color(0xFF1B1B1F)

    // Formatointistringi (esim. "%1$d satua")
    val descFormat = stringResource(R.string.chart_bar_desc_format)

    // 1. Muunnetaan sovelluksen data YCharts BarData -muotoon
    val realBars = uiState.weeklyStats.mapIndexed { index, stat ->
        BarData(
            // X on juokseva numero, Y on satujen määrä
            point = Point(x = (index + 1).toFloat(), y = stat.storyCount.toFloat()),
            color = Color(0xFF2E6B5B), // Tummanvihreä
            label = stat.weekLabel,
            description = String.format(descFormat, stat.storyCount) // Näkyy kun palkkia painetaan
        )
    }

    // Lisätään tyhjät "spacerit" alkuun ja loppuun, jotta ensimmäinen ja viimeinen
    // palkki eivät ole kiinni reunoissa. YChartsilla tämä on joskus tarpeen
    // asettelun kaunistamiseksi.
    val spacerStart = BarData(
        point = Point(x = 0f, y = 0f),
        color = Color.Transparent, label = "", description = ""
    )
    val spacerEnd = BarData(
        point = Point(x = (realBars.size + 1).toFloat(), y = 0f),
        color = Color.Transparent, label = "", description = ""
    )

    val barData = listOf(spacerStart) + realBars + listOf(spacerEnd)

    // Lasketaan X-akselin portaiden määrä
    val xSteps = max(barData.size - 1, 1)

    // 2. Määritellään X-akseli
    val xAxisData = AxisData.Builder()
        .axisStepSize(46.dp) // Palkkien väli
        .steps(xSteps)
        .bottomPadding(16.dp)
        .labelData { index -> barData.getOrNull(index)?.label ?: "" }
        .axisLabelColor(fixedTextColor)
        .axisLineColor(fixedTextColor)
        .build()

    // 3. Määritellään Y-akseli
    // Skaalaus: Pyöristetään ylöspäin lähimpään viiteen
    val rawMax = max(uiState.maxStoryCount, 1)
    val maxY = ((rawMax + 4) / 5) * 5

    val yAxisData = AxisData.Builder()
        .steps(maxY)
        .labelAndAxisLinePadding(24.dp)
        // Näytetään numerot vain joka viidennellä askeleella (0, 5, 10...)
        // jotta akseli ei tukkeudu numeroista
        .labelData { index ->
            if (index % 5 == 0) index.toString() else ""
        }
        .axisLabelColor(fixedTextColor)
        .axisLineColor(fixedTextColor)
        .build()

    // 4. Määritellään itse kaavio
    val barChartData = BarChartData(
        chartData = barData,
        xAxisData = xAxisData,
        yAxisData = yAxisData,
        backgroundColor = Color.Transparent,
        barStyle = BarStyle(
            paddingBetweenBars = 20.dp,
            barWidth = 30.dp
        )
    )

    // Lasketaan kaavion leveys dynaamisesti datamäärän mukaan
    val stepWidth = 46.dp
    val basePadding = 120.dp
    val desiredWidth = (barData.size * stepWidth.value).dp + basePadding
    // Jos leveys on pienempi kuin näyttö, käytetään näytön leveyttä (täyttää ruudun)
    val chartWidth = if (desiredWidth > screenWidth) desiredWidth else screenWidth

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState) // Mahdollistaa skrollauksen
    ) {
        BarChart(
            modifier = Modifier
                .width(chartWidth)
                .height(300.dp),
            barChartData = barChartData
        )
    }
}