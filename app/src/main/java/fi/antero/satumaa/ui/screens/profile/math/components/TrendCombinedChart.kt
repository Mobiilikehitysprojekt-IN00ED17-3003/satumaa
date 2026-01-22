package fi.antero.satumaa.ui.screens.profile.math.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import fi.antero.satumaa.ui.viewmodel.stats.StatsUiState
import kotlin.math.max

@Composable
fun TrendCombinedChart(uiState: StatsUiState) {
    if (uiState.weeklyStats.isEmpty()) return

    val stats = uiState.weeklyStats
    val trendPoints = uiState.trendPoints

    // Asettelu-parametrit
    val barWidth = 30.dp
    val stepX = 75.dp
    val startPadding = 80.dp
    val endPadding = 48.dp
    val topPadding = 20.dp
    val bottomPadding = 70.dp

    val screenWidth = LocalConfiguration.current.screenWidthDp.dp

    // Lasketaan koko leveys datan mukaan, jotta skrollaus toimii oikein
    val contentWidth = startPadding + (stepX * stats.size) + endPadding
    val chartWidth = max(screenWidth.value, contentWidth.value).dp

    // Y-akselin dynaaminen maksimiarvo pyöristettynä seuraavaan 50:een
    val maxAvg = max(uiState.maxAvgLength, 1)
    val yMax = ((maxAvg + 49) / 50) * 50

    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState)
    ) {
        Canvas(
            modifier = Modifier
                .width(chartWidth)
                .height(300.dp)
        ) {
            val h = size.height
            val chartBottom = h - bottomPadding.toPx()
            val chartTop = topPadding.toPx()
            val chartHeight = chartBottom - chartTop

            val startX = startPadding.toPx()
            val barW = barWidth.toPx()
            val step = stepX.toPx()

            // Paint-määritykset teksteille
            val axisLabelPaint = android.graphics.Paint().apply {
                isAntiAlias = true
                color = android.graphics.Color.DKGRAY
                textSize = 30f
                textAlign = android.graphics.Paint.Align.CENTER
            }
            val yValuePaint = android.graphics.Paint().apply {
                isAntiAlias = true
                color = android.graphics.Color.GRAY
                textSize = 28f
                textAlign = android.graphics.Paint.Align.RIGHT
            }
            val xLabelPaint = android.graphics.Paint().apply {
                isAntiAlias = true
                color = android.graphics.Color.BLACK
                textSize = 30f
                textAlign = android.graphics.Paint.Align.CENTER
            }

            // 1. Y-akselin viivat ja arvot (0..4)
            for (i in 0..4) {
                val value = i * (yMax / 4)
                val y = chartBottom - (i.toFloat() / 4f) * chartHeight

                drawLine(
                    color = Color.LightGray.copy(alpha = 0.5f),
                    start = Offset(startX, y),
                    end = Offset(size.width, y),
                    strokeWidth = 1f
                )

                drawIntoCanvas { canvas ->
                    canvas.nativeCanvas.drawText(
                        value.toString(),
                        startX - 15f,
                        y + 10f,
                        yValuePaint
                    )
                }
            }

            // 2. Pylväät ja aikajakson labelit
            stats.forEachIndexed { index, stat ->
                val centerX = startX + (index * step) + (step / 2f)
                val leftX = centerX - (barW / 2f)
                val barHeight = (stat.averageLength.toFloat() / yMax) * chartHeight

                drawRect(
                    color = Color(0xFF6200EE).copy(alpha = 0.4f),
                    topLeft = Offset(leftX, chartBottom - barHeight),
                    size = Size(barW, barHeight)
                )

                drawIntoCanvas { canvas ->
                    canvas.nativeCanvas.drawText(
                        stat.weekLabel,
                        centerX,
                        chartBottom + 35f,
                        xLabelPaint
                    )
                }
            }

            // 3. Trendiviiva (Line)
            if (trendPoints.size >= 2) {
                val path = Path()
                trendPoints.forEachIndexed { i, point ->
                    val centerX = startX + (point.x * step) + (step / 2f)
                    val py = chartBottom - (point.y / yMax) * chartHeight

                    if (i == 0) path.moveTo(centerX, py) else path.lineTo(centerX, py)

                    drawCircle(
                        color = Color.Red,
                        radius = 8f,
                        center = Offset(centerX, py)
                    )
                }
                drawPath(
                    path = path,
                    color = Color.Red,
                    style = Stroke(width = 5f)
                )
            }

            // 4. Akseliviivat (Pääakselit)
            drawLine(
                color = Color.Black,
                start = Offset(startX, chartTop),
                end = Offset(startX, chartBottom),
                strokeWidth = 3f
            )
            drawLine(
                color = Color.Black,
                start = Offset(startX, chartBottom),
                end = Offset(size.width, chartBottom),
                strokeWidth = 3f
            )

            // 5. Akseliselitteet
            drawIntoCanvas { canvas ->

                val xAxisCenter = startX + (size.width - startX) / 3f

                canvas.nativeCanvas.drawText(
                    "Aika (Viikkoa)",
                    xAxisCenter,
                    h - 100f,
                    axisLabelPaint
                )

                // Y-akselin selite (Sanamäärä) pystysuunnassa
                canvas.nativeCanvas.save()
                val centerY = (chartTop + chartBottom) / 2f

                val yLabelXLocation = 100f

                canvas.nativeCanvas.rotate(-90f, yLabelXLocation, centerY)
                canvas.nativeCanvas.drawText(
                    "Keskimääräinen sanamäärä",
                    yLabelXLocation,
                    centerY,
                    axisLabelPaint
                )
                canvas.nativeCanvas.restore()
            }
        }
    }
}