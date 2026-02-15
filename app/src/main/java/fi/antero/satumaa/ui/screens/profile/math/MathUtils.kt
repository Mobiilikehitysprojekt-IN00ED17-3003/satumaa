package fi.antero.satumaa.ui.screens.profile.math

import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * MathUtils: Tekninen apukirjasto (Helper Object).
 *
 * Tämän tiedoston funktiot ovat "puhtaita funktioita" (pure functions):
 * Ne ottavat sisään arvon ja palauttavat tuloksen muuttamatta mitään muuta.
 */
object MathUtils {

    // -----------------------------------------------------------------------
    // AIKALEIMAT JA KALENTERI (Käytetään Pylväs- ja Viivagraafeissa)
    // -----------------------------------------------------------------------

    /**
     * Ongelma: Tietokanta antaa ajan millisekunteina (Long).
     * Ratkaisu: Tämä muuntaa sen muodoon (Vuosi, Viikkonumero).
     *
     * TÄRKEÄÄ: Käytämme Locale("fi", "FI").
     * Jos emme tekisi näin, amerikkalainen kalenteri väittäisi viikon
     * alkavan sunnuntaina, mikä sekoittaisi tilastot suomalaiselle käyttäjälle.
     */
    fun getYearWeekPair(timestamp: Long): Pair<Int, Int> {
        val calendar = Calendar.getInstance(Locale("fi", "FI"))
        calendar.time = Date(timestamp)

        // Asetetaan maanantai viikon ekaksi päiväksi
        calendar.firstDayOfWeek = Calendar.MONDAY
        // Viikossa pitää olla vähintään 4 päivää, jotta se lasketaan uudeksi viikoksi (ISO-8601)
        calendar.minimalDaysInFirstWeek = 4

        val year = calendar.get(Calendar.YEAR)
        val week = calendar.get(Calendar.WEEK_OF_YEAR)

        return Pair(year, week) // Palauttaa esim. (2024, 42)
    }

    /**
     * Sama idea kuin yllä, mutta kuukausille.
     * Käytetään kun käyttäjä valitsee aikajänteeksi "Kuukausi".
     */
    fun getYearMonthPair(timestamp: Long): Pair<Int, Int> {
        val calendar = Calendar.getInstance(Locale("fi", "FI"))
        calendar.time = Date(timestamp)

        val year = calendar.get(Calendar.YEAR)
        // HUOM: Javassa kuukaudet alkavat nollasta (Tammikuu = 0).
        // Lisäämme +1, jotta saamme ihmisille loogisen luvun (Tammikuu = 1).
        val month = calendar.get(Calendar.MONTH) + 1

        return Pair(year, month)
    }

    /**
     * Muuttaa numeron tekstiksi UI:ta varten.
     * 1 -> "Tam", 2 -> "Hel" jne.
     */
    fun getMonthLabel(year: Int, month: Int): String {
        val monthNames = listOf("-", "Tam", "Hel", "Maa", "Huh", "Tou", "Kes", "Hei", "Elo", "Syy", "Lok", "Mar", "Jou")
        // 'getOrElse' estää sovelluksen kaatumisen, jos kuukausi onkin virheellisesti esim. 13
        val name = monthNames.getOrElse(month) { "?" }
        return name
    }

    // -----------------------------------------------------------------------
    // TEKSTIANALYYSI (Käytetään Sanamäärä- ja Seikkailugraafeissa)
    // -----------------------------------------------------------------------

    /**
     * Laskee tekstin pituuden sanoina.
     * Käyttää Regexiä "\\s+", joka tarkoittaa "yksi tai useampi välilyönti".
     * Tämä on fiksumpi kuin pelkkä .split(" "), koska se ei laske tuplavälejä sanoiksi.
     */
    fun countWords(text: String): Int {
        if (text.isBlank()) return 0
        return text.trim().split("\\s+".toRegex()).size
    }

    /**
     * TÄMÄ ON SE "ALGORITMI".
     * Laskee Scatter Plotin Y-akselin arvon (Seikkailupisteet).
     *
     * Logiikka:
     * 1. Etsii "jännittäviä" sanoja (lohikäärme, miekka...).
     * 2. Etsii huutomerkkejä (!).
     * 3. Jakaa tuloksen sanamäärällä (tiheys), jotta lyhyt jännäri voittaa pitkän tylsän tekstin.
     */
    fun calculateAdventureScore(content: String): Double {
        // Lista sanoista, jotka nostavat pisteitä
        val adventureKeywords = listOf(
            "lohikäärme", "miekka", "linna", "avaruus", "rohkea",
            "taika", "luola", "hirviö", "aarre", "pelastaa",
            "sankari", "prinssi", "prinsessa"
        )

        val lowerContent = content.lowercase()
        val wordCount = countWords(content)

        // Estetään nollalla jakaminen (ettei sovellus kaadu tyhjään satuun)
        if (wordCount == 0) return 0.0

        // Lasketaan osumat (Painoarvo: 5 pistettä per sana)
        var keywordHits = 0
        adventureKeywords.forEach { word ->
            if (lowerContent.contains(word)) keywordHits++
        }

        // Lasketaan huutomerkit (Painoarvo: 2 pistettä per huutomerkki)
        val exclamationMarks = content.count { it == '!' }

        // Raakapisteet
        val rawScore = (keywordHits * 5.0) + (exclamationMarks * 2.0)

        // NORMALISOINTI:
        // Jaetaan sanamäärällä, jotta saadaan "jännitystiheys".
        // Kerrotaan sadalla, jotta saadaan kivempi luku (esim. 45 eikä 0.45).
        val densityScore = (rawScore / wordCount) * 100.0

        // Lisätään pieni bonus pituudesta, jotta aivan lyhyet tekstit eivät dominoi liikaa.
        val lengthBonus = wordCount / 50.0

        return densityScore + lengthBonus
    }
}