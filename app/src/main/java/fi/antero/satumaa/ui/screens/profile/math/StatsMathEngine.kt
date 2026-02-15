package fi.antero.satumaa.ui.screens.profile.math

import androidx.compose.ui.graphics.Color
import co.yml.charts.common.model.Point
import fi.antero.satumaa.data.model.Story
import fi.antero.satumaa.ui.theme.*
import org.mariuszgromada.math.mxparser.Argument
import org.mariuszgromada.math.mxparser.Expression
import java.util.Locale
import kotlin.math.max

/**
 * StatsMathEngine: Sovelluksen matemaattinen ydin.
 * Tämä olio vastaa raakadatan (Story-oliot) muuntamisesta tilastollisiksi luvuiksi.
 */
object StatsMathEngine {

    /**
     * LASKENTA 1: Viikkokohtainen aggregointi (Summat ja keskiarvot)
     * Muuttaa epämääräisen listan satuja kronologiseksi tilastoksi.
     */
    fun buildWeeklyStats(range: TimeRange, stories: List<Story>): List<WeeklyStats> {

        // VAIHE 1: Ryhmittely (Grouping)
        // Laskenta alkaa jakamalla sadut "koreihin" ajan mukaan.
        val grouped = stories.groupBy { story ->
            if (range == TimeRange.WEEKLY) {
                val pair = MathUtils.getYearWeekPair(story.createdAt)
                "${pair.first}-W${pair.second}"
            } else {
                val pair = MathUtils.getYearMonthPair(story.createdAt)
                "${pair.first}-${pair.second.toString().padStart(2, '0')}"
            }
        }

        // VAIHE 2: Matemaattinen tiivistys (Aggregation)
        // Käydään jokainen kori läpi ja lasketaan sen sisältö.
        val mapped = grouped.map { (key, storyList) ->

            // LASKU A: Sanamäärien summa. Käydään lista läpi ja lasketaan kokonaissanamäärä.
            val totalWords = storyList.sumOf { MathUtils.countWords(it.content) }

            // LASKU B: Aritmeettinen keskiarvo pituudelle.
            // Kaava: (Sanojen summa) / (Satujen lukumäärä)
            val avgLength = if (storyList.isNotEmpty()) totalWords / storyList.size else 0

            val label = if (range == TimeRange.WEEKLY) {
                val week = key.substringAfter("W")
                "Vk $week"
            } else {
                val month = key.substringAfter("-").toIntOrNull() ?: 1
                MathUtils.getMonthLabel(0, month)
            }

            WeeklyStats(
                weekLabel = label,
                storyCount = storyList.size,    // Satujen määrä (Pylvään Y-arvo)
                averageLength = avgLength       // Keskiarvopituus (Trendiviivan syöte)
            )
        }

        // VAIHE 3: Rajaaminen. Valitaan 6 viimeisintä aikapistettä visualisoinnin selkeyttämiseksi.
        return mapped
            .sortedByDescending { it.weekLabel }
            .take(6)
            .reversed()
    }

    /**
     * LASKENTA 2: Prosenttiosuudet ja frekvenssit (Donitsikaavio)
     */
    fun buildTopKeywords(stories: List<Story>): List<StyleStat> {
        val allKeywords = mutableListOf<String>()

        // Kerätään kaikki sanat yhteen suureen listaan
        for (story in stories) {
            val parts = story.keywords.split(",")
            for (part in parts) {
                val cleaned = part.trim().lowercase()
                if (cleaned.isNotBlank() && cleaned != "a") {
                    allKeywords.add(cleaned)
                }
            }
        }

        // LASKU C: Frekvenssilaskenta. Lasketaan kunkin sanan esiintymiskerrat.
        val keywordCounts = allKeywords.groupingBy { it }.eachCount()
        val pieColors = listOf(Forest, Terracotta, Sky, Gold, ForestDark, Color.Gray)

        return keywordCounts.entries
            .sortedByDescending { it.value }
            .take(5)
            .mapIndexed { index, entry ->
                val rawName = entry.key
                val niceName = rawName.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
                }

                StyleStat(
                    styleName = niceName,
                    count = entry.value,
                    // LASKU D: Suhteellinen osuus prosentteina.
                    // Kaava: (Sanan esiintymät / Kaikkien sanojen summa) * 100
                    percentage = if (allKeywords.isNotEmpty()) (entry.value.toFloat() / allKeywords.size) * 100f else 0f,
                    color = pieColors.getOrElse(index) { Color.Gray }
                )
            }
    }

    /**
     * LASKENTA 3: Hajakuvaajan koordinaatit (X, Y)
     */
    fun buildAdventurePoints(stories: List<Story>): List<AdventurePoint> {
        return stories.map { story ->
            AdventurePoint(
                storyId = story.id,
                title = story.title,
                // X-koordinaatti: Raaka sanamäärä
                wordCount = MathUtils.countWords(story.content).toFloat(),
                // Y-koordinaatti: Algoritminen seikkailupistemäärä (lasketaan MathUtilsissa)
                adventureScore = MathUtils.calculateAdventureScore(story.content).toFloat(),
                styleIcon = getIconForStyle(story.style)
            )
        }
    }

    /**
     * LASKENTA 4: Lineaarinen regressio (Trendiviiva)
     * Tämä on matemaattisesti vaativin osio. Käytetään pienimmän neliösumman menetelmää.
     * Tavoite: Löytää suora y = a + bx, joka on mahdollisimman lähellä kaikkia pisteitä.
     */
    fun buildTrendLineFromAverageLength(stats: List<WeeklyStats>): List<Point> {
        if (stats.size < 2) return emptyList()

        val n = stats.size.toDouble() // Havaintojen määrä
        var sumX = 0.0  // Aikapisteiden summa (0+1+2...)
        var sumY = 0.0  // Pituuksien summa
        var sumXY = 0.0 // Ajan ja pituuden tulon summa
        var sumX2 = 0.0 // Ajan neliöiden summa

        // VAIHE 1: Kerätään tarvittavat summat kaavaa varten
        stats.forEachIndexed { index, stat ->
            val x = index.toDouble()
            val y = stat.averageLength.toDouble()

            sumX += x
            sumY += y
            sumXY += (x * y)
            sumX2 += (x * x)
        }

        // VAIHE 2: Lasketaan kaavan nimittäjä. Jos se on 0, suoraa ei voi muodostaa.
        val denominator = (n * sumX2 - (sumX * sumX))
        if (denominator == 0.0) return emptyList()

        // VAIHE 3: Lasketaan kulmakerroin (b) ja vakiotermi (a).
        // LASKU E: Kulmakerroin (b). Kertoo nouseeko vai laskeeko trendi.
        // Kaava: b = (n*sumXY - sumX*sumY) / (n*sumX2 - sumX^2)
        val slopeB = (n * sumXY - sumX * sumY) / denominator

        // LASKU F: Vakiotermi (a). Kertoo mistä kohdasta viiva alkaa Y-akselilla.
        // Kaava: a = (sumY - b*sumX) / n
        val interceptA = (sumY - slopeB * sumX) / n

        // VAIHE 4: Generoidaan viivan pisteet käyttäen mxparseria.
        // Matemaattinen malli: y = a + b*x
        val xArg = Argument("x")
        val expression = Expression("a + b*x", xArg)
        expression.addArguments(Argument("a", interceptA), Argument("b", slopeB))

        val points = mutableListOf<Point>()
        for (i in stats.indices) {
            xArg.setArgumentValue(i.toDouble())
            // Lasketaan jokaiselle X-akselin kohdalle vastaava Y-arvo viivalla
            points.add(Point(i.toFloat(), expression.calculate().toFloat()))
        }
        return points
    }

    /**
     * LASKENTA 5: Dynaaminen skaalaus
     * Varmistaa, ettei graafi "leikkaa kiinni" yläreunaan.
     */
    fun computeMaxStoryCount(stats: List<WeeklyStats>): Int {
        // LASKU G: Etsitään suurin arvo ja lisätään 20% marginaali yläpuolelle.
        return (stats.maxOfOrNull { it.storyCount } ?: 5) + 2
    }

    fun computeMaxAvgLength(stats: List<WeeklyStats>): Int {
        // LASKU H: Etsitään suurin keskiarvopituus ja lisätään kiinteä marginaali (50).
        val safeMax = (stats.maxOfOrNull { it.averageLength } ?: 100) + 50
        return max(safeMax, 1)
    }

    private fun getIconForStyle(style: String): String = when (style) {
        "EXCITING" -> "⚡"
        "CALMING" -> "😴"
        "FUNNY" -> "🤪"
        "EDUCATIONAL" -> "🦉"
        "GRIMM" -> "🏰"
        "ANDERSEN" -> "🦢"
        "JANSSON" -> "🍃"
        else -> "📜"
    }
}