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

@HiltViewModel
class StatsViewModel @Inject constructor(
    storyRepository: StoryRepository
) : ViewModel() {

    private val _timeRange = MutableStateFlow(TimeRange.WEEKLY)
    val timeRange = _timeRange.asStateFlow()

    private val _uiState = MutableStateFlow(StatsUiState())
    val uiState = _uiState.asStateFlow()

    init {
        combine(_timeRange, storyRepository.getStories()) { range, stories ->
            buildUiState(range, stories)
        }.onEach { newState ->
            _uiState.value = newState
        }.launchIn(viewModelScope)
    }

    fun setTimeRange(range: TimeRange) {
        _timeRange.value = range
    }

    private fun buildUiState(range: TimeRange, stories: List<Story>): StatsUiState {
        if (stories.isEmpty()) return StatsUiState(isLoading = false)

        val statsList = StatsMathEngine.buildWeeklyStats(range, stories)
        val topKeywords = StatsMathEngine.buildTopKeywords(stories)
        val adventureData = StatsMathEngine.buildAdventurePoints(stories)
        val trendPoints = StatsMathEngine.buildTrendLineFromAverageLength(statsList)

        val scatterPoints: List<Point> = adventureData.map { ap ->
            Point(ap.wordCount, ap.adventureScore)
        }

        return StatsUiState(
            isLoading = false,
            weeklyStats = statsList,
            totalStories = stories.size,
            maxStoryCount = StatsMathEngine.computeMaxStoryCount(statsList),
            maxAvgLength = StatsMathEngine.computeMaxAvgLength(statsList),
            topKeywords = topKeywords,
            scatterPoints = scatterPoints,
            adventureData = adventureData,
            trendPoints = trendPoints
        )
    }
}
