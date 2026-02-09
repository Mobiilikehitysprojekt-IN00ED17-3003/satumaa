package fi.antero.satumaa.ui.screens.profile.math.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import fi.antero.satumaa.R
import fi.antero.satumaa.ui.screens.profile.math.AdventurePoint
import fi.antero.satumaa.ui.viewmodel.stats.StatsUiState
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Piirtää "Seikkailuindeksi"-hajakuvaajan (Scatter Plot).
 *
 * X-akseli: Sadun pituus (sanamäärä)
 * Y-akseli: Seikkailupisteet (laskettu avainsanoista MathEnginellä)
 *
 * TOTEUTUS:
 * Käyttää matalan tason Canvas-piirtoa (Native Canvas), koska halusimme
 * täydellisen kontrollin akseleiden, emojien ja kosketuksen (tap gesture) suhteen.
 */
@Composable
fun AdventureScatterChart(uiState: StatsUiState) {
    if (uiState.adventureData.isEmpty()) return

    val points = uiState.adventureData
    val context = LocalContext.current // Tarvitaan Canvasin sisällä tekstiresursseille

    // Tila valitulle pisteelle (näytetään info-laatikko, kun pistettä painetaan)
    var selectedPoint by remember { mutableStateOf<AdventurePoint?>(null) }

    // Lasketaan datan ääriarvot skaalausta varten
    val minX = points.minOf { it.wordCount }
    val maxX = points.maxOf { it.wordCount }
    val minY = 0f // Alku nollasta, jotta skaala on visuaalisesti rehellinen
    val dataMaxY = points.maxOf { it.adventureScore }

    // Turvamarginaalit skaalaukseen (ettei piste mene aivan reunaan)
    val safeMaxX = max(maxX, minX + 1f)
    // Pyöristetään Y-maksimi ylöspäin kymmeniin (esim. 42 -> 50)
    val safeMaxY = max(30f, ((dataMaxY.toInt() / 10) * 10 + 10).toFloat())

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
    ) {
        val density = LocalDensity.current
        val widthPx = with(density) { maxWidth.toPx() }
        val heightPx = with(density) { maxHeight.toPx() }

        // Määritellään marginaalit akseleille ja otsikoille
        val leftPad = 70f
        val rightPad = 30f
        val topPad = 40f
        val bottomPad = 60f

        // Varsinainen piirtoalue (Plot Area)
        val plotLeft = leftPad
        val plotRight = widthPx - rightPad
        val plotTop = topPad
        val plotBottom = heightPx - bottomPad

        // --- APUFUNKTIOT KOORDINAATTIMUUNNOKSIIN ---
        // Muuntaa datan arvon (esim. 150 sanaa) ruudun pikseliksi (esim. x=200px)
        fun mapX(x: Float): Float {
            val t = (x - minX) / (safeMaxX - minX) // Normalisoitu arvo 0..1
            return plotLeft + t * (plotRight - plotLeft)
        }

        // Muuntaa Y-arvon pikseliksi. Huom: Y-akseli kasvaa alaspäin näytöllä,
        // joten 'plotBottom' on 0-arvo ja 'plotTop' on maksimi.
        fun mapY(y: Float): Float {
            val t = (y - minY) / (safeMaxY - minY)
            return plotBottom - t * (plotBottom - plotTop)
        }

        // Haetaan käännökset kerran ennen piirtosilmukkaa
        val xLabel = stringResource(R.string.chart_word_count)
        val yLabel = stringResource(R.string.chart_adventure_index)

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                // Tunnistetaan kosketus
                .pointerInput(Unit) {
                    detectTapGestures { tapOffset ->
                        val touchRadius = 50f // Kuinka läheltä pitää painaa

                        // Etsitään lähin piste
                        val closest = points.minByOrNull { p ->
                            val px = mapX(p.wordCount)
                            val py = mapY(p.adventureScore)
                            // Pythagoraan lause etäisyyden laskemiseen
                            sqrt((px - tapOffset.x).pow(2) + (py - tapOffset.y).pow(2))
                        }

                        // Valitaan piste, jos se on tarpeeksi lähellä
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
            // 1. Piirretään akselit
            drawLine(Color(0xFF444444), Offset(plotLeft, plotBottom), Offset(plotRight, plotBottom), 3f) // X
            drawLine(Color(0xFF444444), Offset(plotLeft, plotBottom), Offset(plotLeft, plotTop), 3f)     // Y

            // 2. Piirretään apuviivat (Grid)
            val tickColor = Color(0x22000000)
            val gridStep = 5f // Viivat 5 pisteen välein
            val numSteps = (safeMaxY / gridStep).toInt()

            // Vaakaviivat
            for (i in 1..numSteps) {
                val yValue = i * gridStep
                val y = mapY(yValue)
                if (y >= plotTop) {
                    drawLine(tickColor, Offset(plotLeft, y), Offset(plotRight, y), 1f)
                }
            }

            // Pystyviivat (4 kpl tasavälein)
            for (i in 1..4) {
                val x = plotLeft + i * (plotRight - plotLeft) / 4f
                drawLine(tickColor, Offset(x, plotBottom), Offset(x, plotTop), 1f)
            }

            // 3. Piirretään datapisteet
            points.forEach { ap ->
                val x = mapX(ap.wordCount)
                val y = mapY(ap.adventureScore)

                // Korostetaan valittua pistettä
                if (ap == selectedPoint) {
                    drawCircle(Color(0x80E91E63), 35f, Offset(x, y))
                } else {
                    drawCircle(Color(0x22E91E63), 18f, Offset(x, y))
                }

                // Piirretään emoji (StyleIcon) pisteen päälle
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

            // 4. Piirretään akseliotsikot
            drawIntoCanvas { canvas ->
                val p = android.graphics.Paint().apply {
                    isAntiAlias = true
                    color = android.graphics.Color.DKGRAY
                    textSize = 30f
                    textAlign = android.graphics.Paint.Align.CENTER
                }
                // X-akselin otsikko
                canvas.nativeCanvas.drawText(xLabel, (plotLeft + plotRight) / 2f, size.height - 15f, p)

                // Y-akselin otsikko (kierretään 90 astetta)
                canvas.nativeCanvas.save()
                canvas.nativeCanvas.rotate(-90f, 25f, (plotTop + plotBottom) / 2f)
                canvas.nativeCanvas.drawText(yLabel, 25f, (plotTop + plotBottom) / 2f, p)
                canvas.nativeCanvas.restore()
            }
        }

        // --- INFO-LAATIKKO (TOOLTIP) ---
        // Näytetään kelluva laatikko, jos piste on valittu
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
                        // Esim. "Pisteet: 45, Sanat: 120"
                        text = stringResource(
                            R.string.chart_points_label,
                            point.adventureScore.toInt(),
                            point.wordCount.toInt()
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.DarkGray
                    )
                }
            }
        }
    }
}