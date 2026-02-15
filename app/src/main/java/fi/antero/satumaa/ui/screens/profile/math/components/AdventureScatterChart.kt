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
 * 4. SEIKKAILUINDEKSI - HAJAKUVAAJA (Scatter Plot)
 *
 * Tämä on sovelluksen teknisesti edistynein UI-komponentti.
 *
 * MIKSI NATIVE CANVAS?
 * Valmiit kirjastot eivät tukeneet emojien piirtämistä datapisteinä tai
 * haluamaamme custom-kosketuslogiikkaa. Siksi piirrämme graafin "käsin"
 * pikseli pikseliltä.
 */
@Composable
fun AdventureScatterChart(uiState: StatsUiState) {
    // 1. Datan validointi: Jos ei dataa, ei piirretä mitään.
    if (uiState.adventureData.isEmpty()) return

    val points = uiState.adventureData
    val context = LocalContext.current

    // TILA (State): Mikä piste on tällä hetkellä valittuna?
    // Tämä ohjaa sitä, näytetäänkö ruudulla Info-laatikko (Tooltip).
    var selectedPoint by remember { mutableStateOf<AdventurePoint?>(null) }

    // --- SKAALAUSLASKENTA (Math Setup) ---
    // Ennen piirtoa meidän täytyy tietää "pelikentän rajat".

    // X-akselin rajat (Sanamäärä): Etsitään lyhin ja pisin satu.
    val minX = points.minOf { it.wordCount }
    val maxX = points.maxOf { it.wordCount }

    // Y-akselin rajat (Pisteet): Alkaa nollasta, loppuu korkeimpaan pistemäärään.
    val minY = 0f
    val dataMaxY = points.maxOf { it.adventureScore }

    // Lasketaan "Turvallinen maksimi".
    // Lisäämme hieman tyhjää tilaa ylös ja oikealle, jotta ylin piste ei leikkaudu puoliksi.
    // X: Lisätään vähintään 1, jotta ei tule nollalla jakamista jos kaikki sadut ovat saman pituisia.
    val safeMaxX = max(maxX, minX + 1f)
    // Y: Pyöristetään ylöspäin lähimpään kymmeneen (esim. 42 -> 50), näyttää siistimmältä.
    val safeMaxY = max(30f, ((dataMaxY.toInt() / 10) * 10 + 10).toFloat())

    // BoxWithConstraints antaa meille komponentin tarkan koon (maxWidth/Height)
    // jota tarvitsemme pikselilaskentaan.
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
    ) {
        val density = LocalDensity.current
        // Muutetaan Dp (laiteriippumaton) -> Px (pikselit)
        val widthPx = with(density) { maxWidth.toPx() }
        val heightPx = with(density) { maxHeight.toPx() }

        // Määritellään marginaalit (Padding), jotta akselien tekstit mahtuvat reunoille.
        val leftPad = 70f   // Tilaa Y-akselin numeroille
        val rightPad = 30f
        val topPad = 40f
        val bottomPad = 60f // Tilaa X-akselin tekstille

        // Lasketaan varsinaisen kuvaaja-alueen (Plot Area) koordinaatit
        val plotLeft = leftPad
        val plotRight = widthPx - rightPad
        val plotTop = topPad
        val plotBottom = heightPx - bottomPad

        // --- MATEMATIIKKA: KOORDINAATTIMUUNNOS (Mapping Functions) ---
        // Nämä funktiot ovat graafin sydän. Ne muuttavat datan (esim. 150 sanaa)
        // ruudun sijainniksi (esim. 320 pikseliä vasemmasta reunasta).

        // X: Lineaarinen interpolaatio vasemmalta oikealle
        fun mapX(x: Float): Float {
            // t = suhdeluku 0..1 (missä kohtaa akselia ollaan)
            val t = (x - minX) / (safeMaxX - minX)
            return plotLeft + t * (plotRight - plotLeft)
        }

        // Y: Lineaarinen interpolaatio alhaalta ylös
        fun mapY(y: Float): Float {
            val t = (y - minY) / (safeMaxY - minY)
            // HUOM: Tietokoneen grafiikassa Y=0 on ylhäällä.
            // Siksi lasku on "Alareuna MIINUS korkeus".
            return plotBottom - t * (plotBottom - plotTop)
        }

        val xLabel = stringResource(R.string.chart_word_count)
        val yLabel = stringResource(R.string.chart_adventure_index)

        // --- PIIRTO (Rendering) ---
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                // --- INTERAKTIO (Touch Handling) ---
                // Tunnistetaan käyttäjän painallus
                .pointerInput(Unit) {
                    detectTapGestures { tapOffset ->
                        val touchRadius = 50f // Osumatarkkuus (sormi on paksu)

                        // Algoritmi: Etsitään lähin piste (Nearest Neighbor Search)
                        val closest = points.minByOrNull { p ->
                            // Lasketaan missä piste sijaitsee pikseleinä
                            val px = mapX(p.wordCount)
                            val py = mapY(p.adventureScore)

                            // Lasketaan etäisyys sormen ja pisteen välillä (Pythagoraan lause)
                            // c = neliöjuuri(a^2 + b^2)
                            sqrt((px - tapOffset.x).pow(2) + (py - tapOffset.y).pow(2))
                        }

                        // Tarkistetaan, osuiko painallus tarpeeksi lähelle
                        if (closest != null) {
                            val px = mapX(closest.wordCount)
                            val py = mapY(closest.adventureScore)
                            val dist = sqrt((px - tapOffset.x).pow(2) + (py - tapOffset.y).pow(2))

                            // Päivitetään tila: Jos osui, valitaan piste. Muuten tyhjennetään valinta.
                            selectedPoint = if (dist < touchRadius) closest else null
                        } else {
                            selectedPoint = null
                        }
                    }
                }
        ) {
            //



            // VAIHE 1: Piirretään akselit (L-muoto)
            drawLine(Color(0xFF444444), Offset(plotLeft, plotBottom), Offset(plotRight, plotBottom), 3f) // X
            drawLine(Color(0xFF444444), Offset(plotLeft, plotBottom), Offset(plotLeft, plotTop), 3f)     // Y

            // VAIHE 2: Piirretään apuviivat (Grid) selkeyden vuoksi
            val tickColor = Color(0x22000000)
            val gridStep = 5f // Viiva joka 5. pisteen välein
            val numSteps = (safeMaxY / gridStep).toInt()

            for (i in 1..numSteps) {
                val yValue = i * gridStep
                val y = mapY(yValue)
                if (y >= plotTop) {
                    drawLine(tickColor, Offset(plotLeft, y), Offset(plotRight, y), 1f)
                }
            }

            //
            // VAIHE 3: Piirretään datapisteet
            points.forEach { ap ->
                val x = mapX(ap.wordCount)
                val y = mapY(ap.adventureScore)

                // Jos piste on valittu, piirretään sen alle korostusympyrä
                if (ap == selectedPoint) {
                    drawCircle(Color(0x80E91E63), 35f, Offset(x, y))
                } else {
                    // Muuten himmeä tausta
                    drawCircle(Color(0x22E91E63), 18f, Offset(x, y))
                }

                // VAIHE 4: Piirretään Emoji (Native Canvas)
                // Käytämme nativeCanvasia, koska Composen drawText on vielä rajoittunut.
                // Haluamme käyttää Androidin Paint-luokkaa emojien renderöintiin.
                drawIntoCanvas { canvas ->
                    val p = android.graphics.Paint().apply {
                        isAntiAlias = true
                        color = android.graphics.Color.BLACK
                        textSize = 34f
                        textAlign = android.graphics.Paint.Align.CENTER
                    }
                    // ap.styleIcon on esimerkiksi "⚡" tai "🦉"
                    canvas.nativeCanvas.drawText(ap.styleIcon, x, y + 12f, p)
                }
            }

            // VAIHE 5: Piirretään akseliotsikot
            drawIntoCanvas { canvas ->
                val p = android.graphics.Paint().apply {
                    isAntiAlias = true
                    color = android.graphics.Color.DKGRAY
                    textSize = 30f
                    textAlign = android.graphics.Paint.Align.CENTER
                }
                // X-akseli: "Sanamäärä"
                canvas.nativeCanvas.drawText(xLabel, (plotLeft + plotRight) / 2f, size.height - 15f, p)

                // Y-akseli: "Seikkailuindeksi"
                // Tämä vaatii koordinaatiston kääntämistä (Rotate), jotta teksti on pystyssä.
                canvas.nativeCanvas.save()
                canvas.nativeCanvas.rotate(-90f, 25f, (plotTop + plotBottom) / 2f)
                canvas.nativeCanvas.drawText(yLabel, 25f, (plotTop + plotBottom) / 2f, p)
                canvas.nativeCanvas.restore() // Palautetaan koordinaatisto normaaliksi
            }
        }

        // --- INFO-LAATIKKO (TOOLTIP / OVERLAY) ---
        // Tämä on normaali Compose-komponentti, joka piirretään Canvasin PÄÄLLE,
        // jos jokin piste on valittuna (selectedPoint != null).
        selectedPoint?.let { point ->
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd) // Asemointi oikeaan yläkulmaan
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