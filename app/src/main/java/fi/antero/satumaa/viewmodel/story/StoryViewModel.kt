package fi.antero.satumaa.ui.viewmodel.story

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fi.antero.satumaa.data.repository.StoryRepository
import fi.antero.satumaa.ui.components.story.StoryLength
import fi.antero.satumaa.ui.components.story.StoryStyle
import fi.antero.satumaa.util.toUserFriendlyMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StoryViewModel @Inject constructor(
    private val repository: StoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<StoryUiState>(StoryUiState.Idle)
    val uiState: StateFlow<StoryUiState> = _uiState.asStateFlow()

    fun generateStory(
        childName: String,
        word1: String,
        word2: String,
        word3: String,
        length: StoryLength,
        style: StoryStyle
    ) {
        viewModelScope.launch {
            val keywords = listOf(word1, word2, word3).map { it.trim() }.filter { it.isNotEmpty() }

            if (keywords.isEmpty()) {
                // Käytetään teknistä koodia, jonka ErrorUtils kääntää
                _uiState.value = StoryUiState.Error(Exception("STORY_KEYWORDS_EMPTY").toUserFriendlyMessage())
                return@launch
            }

            _uiState.value = StoryUiState.Loading


            val result = repository.generateStoryPreview(
                childName = childName,
                keywords = keywords,
                length = length.apiValue,
                style = style.apiValue
            )

            result.onSuccess { previewStory ->
                _uiState.value = StoryUiState.Success(previewStory)
            }.onFailure { e ->
                _uiState.value = StoryUiState.Error(e.toUserFriendlyMessage())
            }
        }
    }

    fun saveCurrentStory() {
        val currentState = _uiState.value
        if (currentState is StoryUiState.Success) {
            val storyToSave = currentState.story

            // Jos satu on jo tallennettu (sillä on ID), ei tehdä mitään
            if (storyToSave.id.isNotEmpty()) return

            viewModelScope.launch {
                val result = repository.saveStory(storyToSave)

                result.onSuccess { newId ->
                    val savedStory = storyToSave.copy(id = newId)
                    _uiState.value = StoryUiState.Success(savedStory)
                }.onFailure { e ->
                    _uiState.value = StoryUiState.Error(e.toUserFriendlyMessage())
                }
            }
        }
    }

    fun loadStory(storyId: String) {
        viewModelScope.launch {
            _uiState.value = StoryUiState.Loading
            try {
                val story = repository.getStory(storyId)
                if (story != null) {
                    _uiState.value = StoryUiState.Success(story)
                } else {
                    // Käytetään teknistä koodia
                    _uiState.value = StoryUiState.Error(Exception("STORY_NOT_FOUND").toUserFriendlyMessage())
                }
            } catch (e: Exception) {
                // Varmistetaan, että tietokantavirheetkin käännetään
                _uiState.value = StoryUiState.Error(e.toUserFriendlyMessage())
            }
        }
    }

    fun resetState() {
        _uiState.value = StoryUiState.Idle
    }
}