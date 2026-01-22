package fi.antero.satumaa.ui.screens.profile.math

import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * MathUtils on tekninen apukirjasto, joka tarjoaa staattisia metodeja
 * datan muokkaamiseen ja analysointiin. Se keskittyy erityisesti
 * aikaleimojen prosessointiin ja tekstipohjaiseen metriikkaan.
 */
object MathUtils {

    /**
     * Muuntaa Unix-aikaleiman (Long) vuoden ja viikon numeron pariksi.
     * Käyttää suomalaista lokaalia varmistaakseen, että viikko alkaa maanantaista
     * ja noudattaa ISO-8601 -standardia (vähintään 4 päivää ensimmäisellä viikolla).
     */
    fun getYearWeekPair(timestamp: Long): Pair<Int, Int> {
        val calendar = Calendar.getInstance(Locale("fi", "FI"))
        calendar.time = Date(timestamp)
        calendar.firstDayOfWeek = Calendar.MONDAY
        calendar.minimalDaysInFirstWeek = 4

        val year = calendar.get(Calendar.YEAR)
        val week = calendar.get(Calendar.WEEK_OF_YEAR)

        return Pair(year, week)
    }

    /**
     * Muuntaa aikaleiman vuoden ja kuukauden pariksi.
     * Palauttaa kuukauden arvovälillä 1-12 (Calendar-luokka käyttää oletuksena 0-11).
     */
    fun getYearMonthPair(timestamp: Long): Pair<Int, Int> {
        val calendar = Calendar.getInstance(Locale("fi", "FI"))
        calendar.time = Date(timestamp)

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1

        return Pair(year, month)
    }

    /**
     * Hakee kuukauden numerolle vastaavan lyhennetyn suomenkielisen nimen.
     * Käytetään X-akselin tunnisteiden generointiin kuukausinäkymässä.
     */
    fun getMonthLabel(year: Int, month: Int): String {
        val monthNames = listOf("-", "Tam", "Hel", "Maa", "Huh", "Tou", "Kes", "Hei", "Elo", "Syy", "Lok", "Mar", "Jou")
        val name = monthNames.getOrElse(month) { "?" }
        return name
    }

    /**
     * Laskee tekstin sanamäärän Regex-pohjaisesti.
     * Hyödyntää "\\s+" -säännöllistä lauseketta, joka tunnistaa yhden tai useamman
     * välilyönnin (mukaan lukien rivinvaihdot) sanan erottimena.
     */
    fun countWords(text: String): Int {
        if (text.isBlank()) return 0
        return text.trim().split("\\s+".toRegex()).size
    }

    /**
     * Algoritmi Seikkailuindeksin (Adventure Score) laskemiseen.
     * * Päivitetty laskentakaava tasapainottamaan pituuden vaikutusta:
     * score = (((avainsanahosumat * 5) + (huutomerkit * 2)) / sanamäärä * 100) + (sanamäärä / 150)
     */
    fun calculateAdventureScore(content: String): Double {
        val adventureKeywords = listOf(
            "lohikäärme", "miekka", "linna", "avaruus", "rohkea",
            "taika", "luola", "hirviö", "aarre", "pelastaa",
            "sankari", "prinssi", "prinsessa"
        )

        val lowerContent = content.lowercase()
        val wordCount = countWords(content)

        // Estetään nollalla jakaminen
        if (wordCount == 0) return 0.0

        // Lasketaan avainsanojen esiintyvyys (painotus 5)
        var keywordHits = 0
        adventureKeywords.forEach { word ->
            if (lowerContent.contains(word)) keywordHits++
        }

        // Lasketaan huutomerkkien määrä (painotus 2)
        val exclamationMarks = content.count { it == '!' }

        // Lasketaan raakapisteet
        val rawScore = (keywordHits * 5.0) + (exclamationMarks * 2.0)

        // Normalisoidaan tiheys ja lisätään pituusbonus tasapainottamaan hajakuvaajaa
        val densityScore = (rawScore / wordCount) * 100.0
        val lengthBonus = wordCount / 50.0

        return densityScore + lengthBonus
    }
}