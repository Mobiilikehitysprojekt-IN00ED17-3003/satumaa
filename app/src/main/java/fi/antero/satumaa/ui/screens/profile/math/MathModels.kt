package fi.antero.satumaa.ui.screens.profile.math

import androidx.compose.ui.graphics.Color

/**
 * Visualisointikerroksen tietomallit.
 * * Nämä mallit on suunniteltu noudattamaan "Separation of Concerns" -periaatetta:
 * Ne erottavat liiketoimintalogiikan (Story-malli) ja käyttöliittymän visualisointitarpeet toisistaan.
 * Tämä mahdollistaa raskaiden laskentojen (kuten sanamäärien analysoinnin) tekemisen taustalla
 * ennen UI-komponenttien piirtämistä.
 */

/**
 * Määrittää tilastojen aikajänteen.
 * Käytetään StatsViewModelissa suodattamaan ja ryhmittelemään dataa dynaamisesti.
 */
enum class TimeRange(val label: String) {
    WEEKLY("Viikko"),
    MONTHLY("Kuukausi")
}

/**
 * Malli aikasarja-analyysille (Pylväs- ja trendikaaviot).
 * Käytetään: WeeklyActivityChart ja TrendCombinedChart.
 * * @param weekLabel X-akselin tunniste (esim. "Vk 4" tai "Tammikuu").
 * @param storyCount Määrällinen muuttuja: kuinka monta dokumenttia luotiin.
 * @param averageLength Laadullinen muuttuja: tekstien matemaattinen keskipituus sanoin.
 */
data class WeeklyStats(
    val weekLabel: String,
    val storyCount: Int,    // Y-akselin korkeus aktiivisuuskaaviossa
    val averageLength: Int  // Y-akselin korkeus kehityskaaviossa (PNS-laskennan pohja)
)

/**
 * Malli kategoriselle datalle (Donitsikaavio).
 * Käytetään: KeywordsPieChart.
 * * @param styleName Kategorian nimi (esim. "Seikkailu").
 * @param count Esiintymiskertojen absoluuttinen määrä.
 * @param percentage Suhteellinen osuus kokonaisuudesta (ympyrän segmentin koko).
 * @param color Teeman mukainen väri, joka on määritetty StatsMathEnginessä.
 */
data class StyleStat(
    val styleName: String,
    val count: Int,
    val percentage: Float,
    val color: Color
)

/**
 * Malli korrelaatioanalyysille (Hajakuvaaja / Scatter Plot).
 * Käytetään: AdventureScatterChart.
 * * Tämä malli toteuttaa Tufte-periaatetta, jossa datapiste (dot) korvataan
 * informatiivisella ikonilla (styleIcon), mikä lisää datan tiheyttä visualisoinnissa.
 * * @param wordCount Riippumaton muuttuja (X-akseli).
 * @param adventureScore Riippuva muuttuja (Y-akseli), laskettu algoritmisesti.
 * @param styleIcon Visuaalinen muuttuja, joka koodaa sadun genren suoraan pisteeseen.
 */
data class AdventurePoint(
    val storyId: String,
    val title: String,
    val wordCount: Float,      // Pituusmuuttuja
    val adventureScore: Float, // Jännitysmuuttuja
    val styleIcon: String      // Kvalitatiivinen merkki (emoji)
)