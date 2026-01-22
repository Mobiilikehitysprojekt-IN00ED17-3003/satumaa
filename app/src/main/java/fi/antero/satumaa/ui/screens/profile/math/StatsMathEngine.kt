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
 * StatsMathEngine on sovelluksen matemaattinen ydin.
 * Se vastaa datan aggregoinnista, tilastollisesta laskennasta (kuten lineaarinen regressio)
 * ja raakadatamuunnoksista UI-komponenteille sopivaan muotoon.
 */
object StatsMathEngine {

    /**
     * Ryhmittelee sadut aikajakson mukaan (viikko tai kuukausi) ja laskee pituustilastot.
     */
    fun buildWeeklyStats(range: TimeRange, stories: List<Story>): List<WeeklyStats> {
        // 1. Ryhmittely: Luodaan avain (vuosi-viikko tai vuosi-kuukausi) luontipäivämäärän perusteella
        val grouped = stories.groupBy { story ->
            if (range == TimeRange.WEEKLY) {
                val pair = MathUtils.getYearWeekPair(story.createdAt)
                "${pair.first}-W${pair.second}"
            } else {
                val pair = MathUtils.getYearMonthPair(story.createdAt)
                "${pair.first}-${pair.second.toString().padStart(2, '0')}"
            }
        }

        // 2. Map-operaatio: Lasketaan kunkin ryhmän sanamäärien summa ja keskiarvo
        val mapped = grouped.map { (key, storyList) ->
            val totalWords = storyList.sumOf { MathUtils.countWords(it.content) }
            val avgLength = if (storyList.isNotEmpty()) totalWords / storyList.size else 0

            // Luodaan käyttäjäystävällinen label (esim. "Vk 4" tai "Tammikuu")
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

        // Järjestetään aikajärjestykseen (laskeva) ja rajoitetaan näkymä 6 viimeisimpään soluun
        return mapped
            .sortedByDescending { it.weekLabel }
            .take(6)
    }

    /**
     * Laskee suosituimmat avainsanat ja valmistelee datan donitsikaaviota varten.
     */
    fun buildTopKeywords(stories: List<Story>): List<StyleStat> {
        val allKeywords = mutableListOf<String>()

        // Kerätään kaikki avainsanat yhteen listaan, siivotaan välilyönnit ja suodatetaan testi-data ("a")
        for (story in stories) {
            val parts = story.keywords.split(",")
            for (part in parts) {
                val cleaned = part.trim().lowercase()
                if (cleaned.isNotBlank() && cleaned != "a") {
                    allKeywords.add(cleaned)
                }
            }
        }

        // Lasketaan kunkin sanan esiintymiskerrat (Frequency count)
        val keywordCounts = allKeywords.groupingBy { it }.eachCount()
        val pieColors = listOf(Forest, Terracotta, Sky, Gold, ForestDark, Color.Gray)

        // Muunnetaan laskennat StyleStat-objekteiksi (top 5 suosituinta)
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
                    // Lasketaan prosentuaalinen osuus kaikista käytetyistä avainsanoista
                    percentage = if (allKeywords.isNotEmpty()) (entry.value.toFloat() / allKeywords.size) * 100f else 0f,
                    color = pieColors.getOrElse(index) { Color.Gray }
                )
            }
    }

    /**
     * Laskee seikkailuindeksin jokaiselle sadulle hajakuvaajaa varten.
     */
    fun buildAdventurePoints(stories: List<Story>): List<AdventurePoint> {
        return stories.map { story ->
            AdventurePoint(
                storyId = story.id,
                title = story.title,
                wordCount = MathUtils.countWords(story.content).toFloat(),
                // AdventureScore perustuu algoritmiin (avainsanat + huutomerkit / pituus)
                adventureScore = MathUtils.calculateAdventureScore(story.content).toFloat(),
                styleIcon = getIconForStyle(story.style)
            )
        }
    }

    /**
     * Lineaarinen regressio (Pienimmän neliösumman menetelmä / OLS).
     * Laskee trendiviivan sadun keskimääräisen pituuden kehitykselle.
     */
    fun buildTrendLineFromAverageLength(stats: List<WeeklyStats>): List<Point> {
        if (stats.size < 2) return emptyList()

        val n = stats.size.toDouble()
        var sumX = 0.0
        var sumY = 0.0
        var sumXY = 0.0
        var sumX2 = 0.0

        // Lasketaan summat regressiokaavaa varten: y = a + bx
        stats.forEachIndexed { index, stat ->
            val x = index.toDouble()
            val y = stat.averageLength.toDouble()
            sumX += x
            sumY += y
            sumXY += (x * y)
            sumX2 += (x * x)
        }

        val denominator = (n * sumX2 - (sumX * sumX))
        if (denominator == 0.0) return emptyList()

        // Lasketaan kulmakerroin (slope) ja vakiotermi (intercept)
        val slopeB = (n * sumXY - sumX * sumY) / denominator
        val interceptA = (sumY - slopeB * sumX) / n

        // Käytetään mXparseria evaluoimaan pisteet trendiviivalle
        val xArg = Argument("x")
        val expression = Expression("a + b*x", xArg)
        expression.addArguments(Argument("a", interceptA), Argument("b", slopeB))

        val points = mutableListOf<Point>()
        for (i in stats.indices) {
            xArg.setArgumentValue(i.toDouble())
            points.add(Point(i.toFloat(), expression.calculate().toFloat()))
        }
        return points
    }

    /**
     * Määrittää Y-akselin maksimikorkeuden aktiivisuuskuvaajalle.
     */
    fun computeMaxStoryCount(stats: List<WeeklyStats>): Int {
        return (stats.maxOfOrNull { it.storyCount } ?: 5) + 2
    }

    /**
     * Määrittää Y-akselin maksimikorkeuden pituuskuvaajalle.
     */
    fun computeMaxAvgLength(stats: List<WeeklyStats>): Int {
        val safeMax = (stats.maxOfOrNull { it.averageLength } ?: 100) + 50
        return max(safeMax, 1)
    }

    /**
     * Apufunktio tyyliä vastaavan emojin hakemiseen.
     */
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