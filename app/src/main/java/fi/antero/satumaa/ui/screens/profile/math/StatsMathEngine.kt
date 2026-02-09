package fi.antero.satumaa.ui.screens.profile.math

import androidx.compose.ui.graphics.Color
import co.yml.charts.common.model.Point
import fi.antero.satumaa.data.model.Story
import fi.antero.satumaa.ui.theme.Forest
import fi.antero.satumaa.ui.theme.ForestDark
import fi.antero.satumaa.ui.theme.Gold
import fi.antero.satumaa.ui.theme.Sky
import fi.antero.satumaa.ui.theme.Terracotta
import org.mariuszgromada.math.mxparser.Argument
import org.mariuszgromada.math.mxparser.Expression
import java.util.Locale
import kotlin.math.max

/**
 * StatsMathEngine on sovelluksen matemaattinen moottori.
 *
 * Se ei tiedä mitään UI:sta tai tietokannasta, vaan se ottaa sisään raakadataa (List<Story>)
 * ja palauttaa pureskeltua tilastotietoa (List<WeeklyStats>, List<Point> jne.).
 */
object StatsMathEngine {

    /**
     * Aggregoi (ryhmittelee) sadut aikajakson mukaan (viikko tai kuukausi).
     * Laskee jokaiselle jaksolle satujen määrän ja keskimääräisen pituuden.
     */
    fun buildWeeklyStats(range: TimeRange, stories: List<Story>): List<WeeklyStats> {
        // 1. Ryhmittely: Luodaan avain (esim. "2024-W42" tai "2024-10")
        val grouped = stories.groupBy { story ->
            if (range == TimeRange.WEEKLY) {
                val pair = MathUtils.getYearWeekPair(story.createdAt)
                "${pair.first}-W${pair.second}"
            } else {
                val pair = MathUtils.getYearMonthPair(story.createdAt)
                "${pair.first}-${pair.second.toString().padStart(2, '0')}"
            }
        }

        // 2. Laskenta: Summataan sanamäärät ja lasketaan keskiarvo per ryhmä
        val mapped = grouped.map { (key, storyList) ->
            val totalWords = storyList.sumOf { MathUtils.countWords(it.content) }
            val avgLength = if (storyList.isNotEmpty()) totalWords / storyList.size else 0

            // Luodaan luettava otsikko (esim. "Vk 42" tai "Lokakuu")
            val label = if (range == TimeRange.WEEKLY) {
                val week = key.substringAfter("W")
                "Vk $week"
            } else {
                val month = key.substringAfter("-").toIntOrNull() ?: 1
                MathUtils.getMonthLabel(0, month)
            }

            WeeklyStats(
                weekLabel = label,
                storyCount = storyList.size,
                averageLength = avgLength
            )
        }

        // Palautetaan 6 uusinta jaksoa aikajärjestyksessä
        return mapped
            .sortedByDescending { it.weekLabel } // Huom: Tämä sorttaus on yksinkertaistettu, oikeasti pitäisi sortata 'key':n mukaan
            .take(6)
            .reversed() // Käännetään, jotta vanhin on vasemmalla (graafia varten)
    }

    /**
     * Analysoi satujen avainsanat ja laskee suosituimmat tyylit donitsikaaviota varten.
     */
    fun buildTopKeywords(stories: List<Story>): List<StyleStat> {
        val allKeywords = mutableListOf<String>()

        // Pilkotaan pilkulla erotetut avainsanat ja siivotaan ne
        for (story in stories) {
            val parts = story.keywords.split(",")
            for (part in parts) {
                val cleaned = part.trim().lowercase()
                // Suodatetaan tyhjät ja roskat (kuten "a")
                if (cleaned.isNotBlank() && cleaned != "a") {
                    allKeywords.add(cleaned)
                }
            }
        }

        // Lasketaan frekvenssit (kuinka monta kertaa mikäkin sana esiintyy)
        val keywordCounts = allKeywords.groupingBy { it }.eachCount()

        // Väripaletti sektoreille
        val pieColors = listOf(Forest, Terracotta, Sky, Gold, ForestDark, Color.Gray)

        // Otetaan top 5 suosituinta
        return keywordCounts.entries
            .sortedByDescending { it.value }
            .take(5)
            .mapIndexed { index, entry ->
                // Muotoillaan sana (alkukirjain isolla)
                val rawName = entry.key
                val niceName = rawName.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
                }

                StyleStat(
                    styleName = niceName,
                    count = entry.value,
                    // Lasketaan prosenttiosuus kokonaismäärästä
                    percentage = if (allKeywords.isNotEmpty()) (entry.value.toFloat() / allKeywords.size) * 100f else 0f,
                    color = pieColors.getOrElse(index) { Color.Gray }
                )
            }
    }

    /**
     * Laskee datan hajakuvaajaa (Scatter Plot) varten.
     * Jokainen satu on piste, jolla on X (pituus) ja Y (seikkailupisteet).
     */
    fun buildAdventurePoints(stories: List<Story>): List<AdventurePoint> {
        return stories.map { story ->
            AdventurePoint(
                storyId = story.id,
                title = story.title,
                wordCount = MathUtils.countWords(story.content).toFloat(),
                // Lasketaan "seikkailupisteet" tekstianalyysillä
                adventureScore = MathUtils.calculateAdventureScore(story.content).toFloat(),
                styleIcon = getIconForStyle(story.style)
            )
        }
    }

    /**
     * LASKENTA: Lineaarinen regressio (Pienimmän neliösumman menetelmä / Least Squares).
     *
     * Tarkoitus: Etsiä suora viiva (y = a + bx), joka kuvaa parhaiten
     * satujen pituuden kehitystä ajan myötä.
     */
    fun buildTrendLineFromAverageLength(stats: List<WeeklyStats>): List<Point> {
        if (stats.size < 2) return emptyList() // Tarvitaan vähintään 2 pistettä viivaan

        val n = stats.size.toDouble()
        var sumX = 0.0
        var sumY = 0.0
        var sumXY = 0.0
        var sumX2 = 0.0

        // X = Aikajakson indeksi (0, 1, 2...)
        // Y = Sadun keskimääräinen pituus
        stats.forEachIndexed { index, stat ->
            val x = index.toDouble()
            val y = stat.averageLength.toDouble()
            sumX += x
            sumY += y
            sumXY += (x * y)
            sumX2 += (x * x)
        }

        // Regressiokaavojen nimittäjä
        val denominator = (n * sumX2 - (sumX * sumX))
        if (denominator == 0.0) return emptyList()

        // Lasketaan kulmakerroin (b) ja vakiotermi (a)
        // Kaava: y = a + bx
        val slopeB = (n * sumXY - sumX * sumY) / denominator
        val interceptA = (sumY - slopeB * sumX) / n

        // Käytetään mXparser-kirjastoa arvojen laskemiseen (hieman overkill tässä,
        // mutta näyttää miten kirjastoa käytetään).
        val xArg = Argument("x")
        val expression = Expression("a + b*x", xArg)
        expression.addArguments(Argument("a", interceptA), Argument("b", slopeB))

        val points = mutableListOf<Point>()
        for (i in stats.indices) {
            xArg.setArgumentValue(i.toDouble())
            // Lasketaan Y-arvo (ennuste) jokaiselle X-kohdalle
            points.add(Point(i.toFloat(), expression.calculate().toFloat()))
        }
        return points
    }

    // --- APUFUNKTIOT GRAAFIEN SKAALAUKSEEN ---

    fun computeMaxStoryCount(stats: List<WeeklyStats>): Int {
        return (stats.maxOfOrNull { it.storyCount } ?: 5) + 2
    }

    fun computeMaxAvgLength(stats: List<WeeklyStats>): Int {
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