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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import fi.antero.satumaa.R
import fi.antero.satumaa.ui.viewmodel.stats.StatsUiState
import kotlin.math.max

/**
 * Piirtää yhdistelmäkaavion: Pylväät (sadun pituus) + Trendiviiva.
 *
 * Tarkoitus: Visualisoida, pitenevätkö lapsen sadut ajan myötä (trendi).
 * Toteutus: Canvas (piirretään itse), koska valmiit kirjastot harvoin tukevat
 * näin spesifiä yhdistelmää helposti.
 */
@Composable
fun TrendCombinedChart(uiState: StatsUiState) {
    if (uiState.weeklyStats.isEmpty()) return

    val stats = uiState.weeklyStats
    val trendPoints = uiState.trendPoints // Pienimmän neliösumman menetelmällä lasketut pisteet

    // Asettelu-vakiot (dp)
    val barWidth = 30.dp
    val stepX = 75.dp // Pylväiden etäisyys toisistaan
    val startPadding = 80.dp // Tilaa Y-akselin numeroille
    val endPadding = 48.dp
    val topPadding = 20.dp
    val bottomPadding = 70.dp // Tilaa X-akselin teksteille

    // Lasketaan kaavion kokonaisleveys sisällön perusteella
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val contentWidth = startPadding + (stepX * stats.size) + endPadding
    // Jos sisältö on leveämpi kuin ruutu, tehdään siitä skrollattava
    val chartWidth = max(screenWidth.value, contentWidth.value).dp

    // Skaalaus Y-akselilla (pyöristetään ylöspäin lähimpään 50:een)
    val maxAvg = max(uiState.maxAvgLength, 1)
    val yMax = ((maxAvg + 49) / 50) * 50

    val scrollState = rememberScrollState()

    // Haetaan otsikot resursseista
    val xAxisLabel = stringResource(R.string.chart_trend_xaxis)
    val yAxisLabel = stringResource(R.string.chart_trend_yaxis)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState) // Mahdollistaa sivuttais-scrollauksen
    ) {
        Canvas(
            modifier = Modifier
                .width(chartWidth)
                .height(300.dp)
        ) {
            val h = size.height
            val w = size.width

            // Piirtoalueen rajat pikseleinä
            val chartBottom = h - bottomPadding.toPx()
            val chartTop = topPadding.toPx()
            val chartHeight = chartBottom - chartTop

            val startX = startPadding.toPx()
            val barW = barWidth.toPx()
            val step = stepX.toPx()

            // Tekstityylit (Android Native Paint)
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

            // 1. Piirretään Y-akseli ja vaakaviivat (Grid)
            // Jaetaan Y-akseli 4 osaan
            for (i in 0..4) {
                val value = i * (yMax / 4)
                val y = chartBottom - (i.toFloat() / 4f) * chartHeight

                // Vaakaviiva
                drawLine(
                    color = Color.LightGray.copy(alpha = 0.5f),
                    start = Offset(startX, y),
                    end = Offset(w, y),
                    strokeWidth = 1f
                )

                // Numeroarvo vasemmalle
                drawIntoCanvas { canvas ->
                    canvas.nativeCanvas.drawText(
                        value.toString(),
                        startX - 15f,
                        y + 10f,
                        yValuePaint
                    )
                }
            }

            // 2. Piirretään Pylväät (Bar Chart)
            stats.forEachIndexed { index, stat ->
                val centerX = startX + (index * step) + (step / 2f)
                val leftX = centerX - (barW / 2f)

                // Pylvään korkeus suhteessa maksimiin
                val barHeight = (stat.averageLength.toFloat() / yMax) * chartHeight

                drawRect(
                    color = Color(0xFF6200EE).copy(alpha = 0.4f), // Puoliläpinäkyvä lila
                    topLeft = Offset(leftX, chartBottom - barHeight),
                    size = Size(barW, barHeight)
                )

                // Viikon/Kuukauden nimi alle
                drawIntoCanvas { canvas ->
                    canvas.nativeCanvas.drawText(
                        stat.weekLabel,
                        centerX,
                        chartBottom + 35f,
                        xLabelPaint
                    )
                }
            }

            // 3. Piirretään Trendiviiva (Line Chart) päällimmäiseksi
            if (trendPoints.size >= 2) {
                val path = Path()

                trendPoints.forEachIndexed { i, point ->
                    // Muunnetaan loogiset koordinaatit pikseleiksi
                    val centerX = startX + (point.x * step) + (step / 2f)
                    val py = chartBottom - (point.y / yMax) * chartHeight

                    if (i == 0) path.moveTo(centerX, py) else path.lineTo(centerX, py)

                    // Piirretään piste viivalle
                    drawCircle(
                        color = Color.Red,
                        radius = 8f,
                        center = Offset(centerX, py)
                    )
                }

                // Piirretään itse viiva
                drawPath(
                    path = path,
                    color = Color.Red,
                    style = Stroke(width = 5f)
                )
            }

            // 4. Piirretään akseliviivat (X ja Y)
            drawLine(
                color = Color.Black,
                start = Offset(startX, chartTop),
                end = Offset(startX, chartBottom),
                strokeWidth = 3f
            )
            drawLine(
                color = Color.Black,
                start = Offset(startX, chartBottom),
                end = Offset(w, chartBottom),
                strokeWidth = 3f
            )

            // 5. Piirretään akseliselitteet (Axis Titles)
            drawIntoCanvas { canvas ->
                // X-akselin otsikko
                val xAxisCenter = startX + (w - startX) / 3f // Keskitetään suunnilleen datan alle
                canvas.nativeCanvas.drawText(
                    xAxisLabel,
                    xAxisCenter,
                    h - 20f, // Melkein alareunassa
                    axisLabelPaint
                )

                // Y-akselin otsikko (Kierretään 90 astetta)
                canvas.nativeCanvas.save()
                val centerY = (chartTop + chartBottom) / 2f
                val yLabelXLocation = 30f // Aivan vasemmassa reunassa

                canvas.nativeCanvas.rotate(-90f, yLabelXLocation, centerY)
                canvas.nativeCanvas.drawText(
                    yAxisLabel,
                    yLabelXLocation,
                    centerY,
                    axisLabelPaint
                )
                canvas.nativeCanvas.restore()
            }
        }
    }
}