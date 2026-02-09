package fi.antero.satumaa.ui.viewmodel.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.yml.charts.common.model.Point
import dagger.hilt.android.lifecycle.HiltViewModel
import fi.antero.satumaa.data.model.Story
import fi.antero.satumaa.data.repository.StoryRepository
import fi.antero.satumaa.ui.screens.profile.math.StatsMathEngine
import fi.antero.satumaa.ui.screens.profile.math.TimeRange
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

/**
 * ViewModel tilasto-osiolle.
 *
 * Tämän luokan vastuulla on:
 * 1. Kuunnella satujen muutoksia tietokannasta (Repository).
 * 2. Kuunnella käyttäjän valitsemaa aikajännettä (Viikko/Kuukausi).
 * 3. Delegoida raskas laskenta `StatsMathEngine`:lle.
 * 4. Tuottaa lopullinen `StatsUiState` käyttöliittymälle.
 */
@HiltViewModel
class StatsViewModel @Inject constructor(
    storyRepository: StoryRepository
) : ViewModel() {

    // Käyttäjän valinta: Näytetäänkö data viikoittain vai kuukausittain
    private val _timeRange = MutableStateFlow(TimeRange.WEEKLY)
    val timeRange = _timeRange.asStateFlow()

    // UI:n tila
    private val _uiState = MutableStateFlow(StatsUiState())
    val uiState = _uiState.asStateFlow()

    init {
        // REAKTIIVINEN OHJELMOINTI (Reactive Programming):
        // combine-operaattori yhdistää kaksi tietovirtaa:
        // 1. Käyttäjän valitsema aikajänne (_timeRange)
        // 2. Tietokannasta tulevat sadut (getStories)
        //
        // Aina kun JOMPIKUMPI muuttuu, suoritetaan lohko, joka laskee tilastot uudelleen.
        combine(_timeRange, storyRepository.getStories()) { range, stories ->
            buildUiState(range, stories)
        }.onEach { newState ->
            // Päivitetään UI-tila uudella lasketulla datalla
            _uiState.value = newState
        }.launchIn(viewModelScope) // Sidotaan ViewModelin elinkaareen
    }

    /**
     * Käyttäjän toiminto: Vaihda aikajännettä (Viikko <-> Kuukausi).
     * Tämä liipaisee yllä olevan 'combine'-ketjun uudelleen.
     */
    fun setTimeRange(range: TimeRange) {
        _timeRange.value = range
    }

    /**
     * Yksityinen funktio, joka kokoaa raakadatasta UI-tilan.
     * Tämä funktio ei tee laskentaa itse, vaan toimii "rakennusmestarina"
     * ja pyytää StatsMathEnginea (insinööriä) laskemaan luvut.
     */
    private fun buildUiState(range: TimeRange, stories: List<Story>): StatsUiState {
        if (stories.isEmpty()) return StatsUiState(isLoading = false)

        // Delegoidaan laskenta MathEnginelle
        val statsList = StatsMathEngine.buildWeeklyStats(range, stories)
        val topKeywords = StatsMathEngine.buildTopKeywords(stories)
        val adventureData = StatsMathEngine.buildAdventurePoints(stories)
        val trendPoints = StatsMathEngine.buildTrendLineFromAverageLength(statsList)

        // Muunnetaan sovelluksen omat mallit graafikirjaston ymmärtämiksi Point-objekteiksi
        val scatterPoints: List<Point> = adventureData.map { ap ->
            Point(ap.wordCount, ap.adventureScore)
        }

        return StatsUiState(
            isLoading = false,
            weeklyStats = statsList,
            totalStories = stories.size,
            // Lasketaan skaalausarvot
            maxStoryCount = StatsMathEngine.computeMaxStoryCount(statsList),
            maxAvgLength = StatsMathEngine.computeMaxAvgLength(statsList),
            topKeywords = topKeywords,
            scatterPoints = scatterPoints,
            adventureData = adventureData,
            trendPoints = trendPoints
        )
    }
}