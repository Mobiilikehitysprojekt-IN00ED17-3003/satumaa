package fi.antero.satumaa.ui.screens.profile.math

import androidx.compose.ui.graphics.Color

/**
 * MathModels.kt - Graafien rakennuspalikat.
 *
 * Tämän tiedoston rooli on toimia VÄLIKERROKSENA (Intermediate Layer).
 *
 * 1. Tietokanta antaa meille 'Story'-olioita.
 * 2. MathEngine pureskelee ne.
 * 3. MathEngine sylkee ulos näitä 'Stats'-olioita.
 * 4. UI (Compose) piirtää nämä oliot ruudulle.
 */

// -----------------------------------------------------------------------
// 1. OHJAUS (Control)
// -----------------------------------------------------------------------

/**
 * Käyttäjän valinta: "Haluanko nähdä viikot vai kuukaudet?"
 *
 * Tämä ei ole vain UI-juttu, vaan tämä enum ohjaa MathEnginen
 * ryhmittelylogiikkaa (groupBy).
 */
enum class TimeRange(val label: String) {
    WEEKLY("Viikko"),   // Ryhmittele viikkonumeron mukaan (esim. 42)
    MONTHLY("Kuukausi") // Ryhmittele kuukauden mukaan (esim. Lokakuu)
}

// -----------------------------------------------------------------------
// 2. AIKASARJAT (Time Series Data)
// -----------------------------------------------------------------------

/**
 * Tämä luokka edustaa YHTÄ PYLVÄSTÄ graafissa.
 *
 * Käytetään kahdessa eri graafissa:
 * 1. ActivityChart: Käyttää 'storyCount' (Pylvään korkeus)
 * 2. TrendChart: Käyttää 'averageLength' (Viivan korkeus)
 */
data class WeeklyStats(
    // X-AKSELI: Mitä lukee pylvään alla? (Esim. "Vk 42")
    val weekLabel: String,

    // Y-AKSELI (Vasen): Montako satua tällä viikolla tehtiin?
    val storyCount: Int,

    // Y-AKSELI (Oikea): Kuinka pitkiä sadut olivat keskimäärin?
    // Tämä on valmiiksi laskettu MathEnginessä, jotta UI:n ei tarvitse laskea.
    val averageLength: Int
)

// -----------------------------------------------------------------------
// 3. KATEGORIAT (Categorical Data)
// -----------------------------------------------------------------------

/**
 * Tämä luokka edustaa YHTÄ SEKTORIA (Slice) donitsikaaviossa.
 *
 * UI on tässä "tyhmä": Se ei päätä värejä eikä laske prosentteja.
 * MathEngine on jo tehnyt ne valmiiksi tähän olioon.
 */
data class StyleStat(
    // Mikä kategoria? (Esim. "Seikkailu")
    val styleName: String,

    // Raaka lukumäärä (Tooltipia varten, esim. "5 kpl")
    val count: Int,

    // Valmis prosenttiluku (Sektorin koko, esim. 25.5%)
    val percentage: Float,

    // Sektorin väri (Määritelty teeman mukaan MathEnginessä)
    val color: Color
)

// -----------------------------------------------------------------------
// 4. KORRELAATIO (XY Data)
// -----------------------------------------------------------------------

/**
 * Tämä luokka edustaa YHTÄ PISTETTÄ (tai emojia) hajakuvaajassa.
 *
 * Tässä toteutuu hieno datan visualisointiperiaate:
 * Yksi piste kertoo kolme asiaa kerralla:
 * 1. X-sijainti = Pituus
 * 2. Y-sijainti = Jännitys
 * 3. Ikoni = Tyyli (Genre)
 */
data class AdventurePoint(
    // Tarvitaan, jotta tiedetään mikä satu on kyseessä (jos halutaan avata se)
    val storyId: String,
    val title: String,

    // X-koordinaatti
    val wordCount: Float,

    // Y-koordinaatti (Algoritmin tulos)
    val adventureScore: Float,

    // Graafinen elementti (Esim. ⚡ tai 🦉)
    val styleIcon: String
)