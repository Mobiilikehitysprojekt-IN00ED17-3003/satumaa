package fi.antero.satumaa.ui.viewmodel.stats

import co.yml.charts.common.model.Point
import fi.antero.satumaa.ui.screens.profile.math.AdventurePoint
import fi.antero.satumaa.ui.screens.profile.math.StyleStat
import fi.antero.satumaa.ui.screens.profile.math.WeeklyStats

/**
 * StatsUiState sijaitsee täällä (viewmodel-paketissa), koska se on
 * ViewModelin sopimus käyttöliittymän kanssa.
 * Se kuitenkin käyttää math-paketin malleja (WeeklyStats, jne.),
 * jotta opettaja näkee datamallinnuksen siellä.
 */
data class StatsUiState(
    val isLoading: Boolean = true,

    // 1. Pylväsdiagrammi & Combined Chart
    val weeklyStats: List<WeeklyStats> = emptyList(),
    val totalStories: Int = 0,

    // Skaalausarvot graafeille (lasketaan dynaamisesti ViewModelissa)
    val maxStoryCount: Int = 10,
    val maxAvgLength: Int = 500,

    // 2. Piirakkakaavio
    val topKeywords: List<StyleStat> = emptyList(),

    // 3. Hajakuvaaja (Seikkailuindeksi)
    // YCharts tarvitsee Point-listan piirtämiseen
    val scatterPoints: List<Point> = emptyList(),
    // UI tarvitsee lisätiedot (ikonit, otsikot) tooltipiä varten
    val adventureData: List<AdventurePoint> = emptyList(),

    // 4. Trendiviiva (Matemaattinen malli)
    val trendPoints: List<Point> = emptyList()
)