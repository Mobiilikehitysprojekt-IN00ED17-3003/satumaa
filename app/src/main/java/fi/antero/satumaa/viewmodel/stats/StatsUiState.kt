package fi.antero.satumaa.ui.viewmodel.stats

import co.yml.charts.common.model.Point
import fi.antero.satumaa.ui.screens.profile.math.AdventurePoint
import fi.antero.satumaa.ui.screens.profile.math.StyleStat
import fi.antero.satumaa.ui.screens.profile.math.WeeklyStats

/**
 * Tilastojen UI-tila (State).
 *
 * Tämä luokka määrittelee kaiken datan, mitä 'ProfileMathSection' tarvitsee piirtääkseen
 * graafit ja kaaviot.
 *
 * Arkkitehtuurinen huomio:
 * Vaikka tämä luokka sijaitsee viewmodel-paketissa, se hyödyntää `ui.screens.profile.math`
 * -paketissa määriteltyjä tietomalleja (WeeklyStats, AdventurePoint).
 * Tämä jakaa vastuun:
 * - `math`-paketti: Matemaattinen mallinnus ja logiikka.
 * - `viewmodel`-paketti: Datan sitominen UI:lle (State Management).
 */
data class StatsUiState(
    // Kertoo, onko laskenta tai datanhaku vielä kesken
    val isLoading: Boolean = true,

    // --- 1. Pylväsdiagrammi (Aktiivisuus) & Yhdistelmäkaavio (Trendi) ---
    // Listaus viikoittaisista/kuukausittaisista tilastoista
    val weeklyStats: List<WeeklyStats> = emptyList(),
    // Kokonaismäärä (esim. "Olet luonut 15 satua")
    val totalStories: Int = 0,

    // Graafien Y-akselin maksimiarvot.
    // Nämä lasketaan dynaamisesti datan perusteella StatsMathEnginessä,
    // jotta graafit skaalautuvat kauniisti ruudulle.
    val maxStoryCount: Int = 10,
    val maxAvgLength: Int = 500,

    // --- 2. Piirakkakaavio (Mieltymykset) ---
    // Suosituimmat avainsanat ja niiden osuudet
    val topKeywords: List<StyleStat> = emptyList(),

    // --- 3. Hajakuvaaja (Seikkailuindeksi) ---
    // 'scatterPoints': YCharts-kirjaston vaatima raaka data (X, Y) -koordinaatit.
    val scatterPoints: List<Point> = emptyList(),
    // 'adventureData': Sovelluksen oma rikas data (otsikko, emoji), jota käytetään
    // kun käyttäjä klikkaa pistettä (Tooltip/Info-laatikko).
    val adventureData: List<AdventurePoint> = emptyList(),

    // --- 4. Trendiviiva (Matemaattinen malli) ---
    // Pienimmän neliösumman menetelmällä (OLS) lasketut pisteet regressiosuoralle.
    val trendPoints: List<Point> = emptyList()
)