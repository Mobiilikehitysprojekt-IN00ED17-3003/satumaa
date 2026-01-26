package fi.antero.satumaa.ui.screens.profile.math.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import fi.antero.satumaa.ui.screens.profile.math.AdventurePoint
import fi.antero.satumaa.ui.viewmodel.stats.StatsUiState
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.sqrt

@Composable
fun AdventureScatterChart(uiState: StatsUiState) {
    if (uiState.adventureData.isEmpty()) return

    val points = uiState.adventureData

    // Tila valitulle pisteelle (näytetään info kun painetaan)
    var selectedPoint by remember { mutableStateOf<AdventurePoint?>(null) }

    val minX = points.minOf { it.wordCount }
    val maxX = points.maxOf { it.wordCount }
    val minY = 0f // Alku nollasta, jotta skaala on rehellinen
    val dataMaxY = points.maxOf { it.adventureScore }

    val safeMaxX = max(maxX, minX + 1f)

    // Nostetaan Y-akselin maksimi piste
    val safeMaxY = max(30f, ((dataMaxY.toInt() / 10) * 10 + 10).toFloat())

    // BoxWithConstraints tarvitaan, jotta tiedämme leveyden (maxWidth) kosketusta varten
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
    ) {
        val density = LocalDensity.current
        val widthPx = with(density) { maxWidth.toPx() }
        val heightPx = with(density) { maxHeight.toPx() }

        // Asettelu-parametrit
        val leftPad = 70f
        val rightPad = 30f
        val topPad = 40f
        val bottomPad = 60f

        val plotLeft = leftPad
        val plotRight = widthPx - rightPad
        val plotTop = topPad
        val plotBottom = heightPx - bottomPad

        // Muunnosfunktiot (Data -> Pikselit)
        fun mapX(x: Float): Float {
            val t = (x - minX) / (safeMaxX - minX)
            return plotLeft + t * (plotRight - plotLeft)
        }

        fun mapY(y: Float): Float {
            val t = (y - minY) / (safeMaxY - minY)
            return plotBottom - t * (plotBottom - plotTop)
        }

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures { tapOffset ->
                        val touchRadius = 50f

                        val closest = points.minByOrNull { p ->
                            val px = mapX(p.wordCount)
                            val py = mapY(p.adventureScore)
                            sqrt((px - tapOffset.x).pow(2) + (py - tapOffset.y).pow(2))
                        }

                        if (closest != null) {
                            val px = mapX(closest.wordCount)
                            val py = mapY(closest.adventureScore)
                            val dist = sqrt((px - tapOffset.x).pow(2) + (py - tapOffset.y).pow(2))

                            selectedPoint = if (dist < touchRadius) closest else null
                        } else {
                            selectedPoint = null
                        }
                    }
                }
        ) {
            // 1. Akselit
            drawLine(Color(0xFF444444), Offset(plotLeft, plotBottom), Offset(plotRight, plotBottom), 3f)
            drawLine(Color(0xFF444444), Offset(plotLeft, plotBottom), Offset(plotLeft, plotTop), 3f)

            // 2. Asteikkoviivat (Grid)
            val tickColor = Color(0x22000000)
            val gridStep = 5f // Haluttu väli pisteissä
            val numSteps = (safeMaxY / gridStep).toInt()

            for (i in 1..numSteps) {
                val yValue = i * gridStep
                val y = mapY(yValue)


                if (y >= plotTop) {
                    drawLine(tickColor, Offset(plotLeft, y), Offset(plotRight, y), 1f)
                }
            }

            // X-akselin pystyviivat
            for (i in 1..4) {
                val x = plotLeft + i * (plotRight - plotLeft) / 4f
                drawLine(tickColor, Offset(x, plotBottom), Offset(x, plotTop), 1f)
            }

            // 3. Pisteet
            points.forEach { ap ->
                val x = mapX(ap.wordCount)
                val y = mapY(ap.adventureScore)

                if (ap == selectedPoint) {
                    drawCircle(Color(0x80E91E63), 35f, Offset(x, y))
                } else {
                    drawCircle(Color(0x22E91E63), 18f, Offset(x, y))
                }

                drawIntoCanvas { canvas ->
                    val p = android.graphics.Paint().apply {
                        isAntiAlias = true
                        color = android.graphics.Color.BLACK
                        textSize = 34f
                        textAlign = android.graphics.Paint.Align.CENTER
                    }
                    canvas.nativeCanvas.drawText(ap.styleIcon, x, y + 12f, p)
                }
            }

            // 4. Akseliotsikot
            drawIntoCanvas { canvas ->
                val p = android.graphics.Paint().apply {
                    isAntiAlias = true
                    color = android.graphics.Color.DKGRAY
                    textSize = 30f
                    textAlign = android.graphics.Paint.Align.CENTER
                }
                canvas.nativeCanvas.drawText("Sanamäärä", (plotLeft + plotRight) / 2f, size.height - 15f, p)

                canvas.nativeCanvas.save()
                canvas.nativeCanvas.rotate(-90f, 25f, (plotTop + plotBottom) / 2f)
                canvas.nativeCanvas.drawText("Seikkailuindeksi", 25f, (plotTop + plotBottom) / 2f, p)
                canvas.nativeCanvas.restore()
            }
        }

        // --- INFO-LAATIKKO ---
        selectedPoint?.let { point ->
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 10.dp, end = 10.dp)
                    .background(Color.White.copy(alpha = 0.95f), RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${point.styleIcon} ${point.title}",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = "Pisteet: ${point.adventureScore.toInt()} | Sanat: ${point.wordCount.toInt()}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.DarkGray
                    )
                }
            }
        }
    }
}