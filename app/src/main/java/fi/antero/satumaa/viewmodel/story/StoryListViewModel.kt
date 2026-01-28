package fi.antero.satumaa.ui.viewmodel.story

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fi.antero.satumaa.data.repository.StoryRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StoryListViewModel @Inject constructor(
    private val repository: StoryRepository
) : ViewModel() {


    val stories = repository.getStories()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        // Kun ViewModel luodaan (käyttäjä avaa listan), aloitetaan synkronointi taustalla.
        refreshStories()
    }

    fun refreshStories() {
        viewModelScope.launch {
            repository.refreshStories()
        }
    }

    fun deleteStory(storyId: String) {
        viewModelScope.launch {
            repository.deleteStory(storyId)
        }
    }
}