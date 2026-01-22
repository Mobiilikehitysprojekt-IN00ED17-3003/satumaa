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
import androidx.compose.ui.unit.dp
import co.yml.charts.axis.AxisData
import co.yml.charts.common.model.Point
import co.yml.charts.ui.barchart.BarChart
import co.yml.charts.ui.barchart.models.BarChartData
import co.yml.charts.ui.barchart.models.BarData
import co.yml.charts.ui.barchart.models.BarStyle
import fi.antero.satumaa.ui.viewmodel.stats.StatsUiState
import kotlin.math.max

@Composable
fun WeeklyActivityChart(uiState: StatsUiState) {
    if (uiState.weeklyStats.isEmpty()) return

    val scrollState = rememberScrollState()
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp


    val fixedTextColor = Color(0xFF1B1B1F)

    val realBars = uiState.weeklyStats.mapIndexed { index, stat ->
        BarData(
            point = Point(x = (index + 1).toFloat(), y = stat.storyCount.toFloat()),
            color = Color(0xFF2E6B5B),
            label = stat.weekLabel,
            description = "${stat.storyCount} satua"
        )
    }


    val spacerStart = BarData(
        point = Point(x = 0f, y = 0f),
        color = Color.Transparent,
        label = "",
        description = ""
    )

    val spacerEnd = BarData(
        point = Point(x = (realBars.size + 1).toFloat(), y = 0f),
        color = Color.Transparent,
        label = "",
        description = ""
    )

    val barData = listOf(spacerStart) + realBars + listOf(spacerEnd)

    val xSteps = max(barData.size - 1, 1)

    val xAxisData = AxisData.Builder()
        .axisStepSize(46.dp)
        .steps(xSteps)
        .bottomPadding(16.dp)
        .labelData { index -> barData.getOrNull(index)?.label ?: "" }
        .axisLabelColor(fixedTextColor)
        .axisLineColor(fixedTextColor)
        .build()

    val rawMax = max(uiState.maxStoryCount, 1)
    val maxY = ((rawMax + 4) / 5) * 5

    val yAxisData = AxisData.Builder()
        .steps(maxY)
        .labelAndAxisLinePadding(24.dp)
        .labelData { index ->
            if (index % 5 == 0) index.toString() else ""
        }
        .axisLabelColor(fixedTextColor)
        .axisLineColor(fixedTextColor)
        .build()

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

    val stepWidth = 46.dp
    val basePadding = 120.dp
    val desiredWidth = (barData.size * stepWidth.value).dp + basePadding
    val chartWidth = if (desiredWidth > screenWidth) desiredWidth else screenWidth

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState)
    ) {
        BarChart(
            modifier = Modifier
                .width(chartWidth)
                .height(300.dp),
            barChartData = barChartData
        )
    }
}