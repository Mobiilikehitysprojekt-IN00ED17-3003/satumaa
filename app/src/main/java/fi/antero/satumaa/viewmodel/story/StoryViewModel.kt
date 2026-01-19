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
                _uiState.value = StoryUiState.Error("Kirjoita ainakin yksi taikasana!")
                return@launch
            }

            _uiState.value = StoryUiState.Loading


            try {
                val result = repository.generateAndSaveStory(
                    childName = childName,
                    keywords = keywords,
                    length = length.apiValue,
                    style = style.apiValue
                )

                result.onSuccess { storyId ->
                    loadStory(storyId)
                }.onFailure { e ->

                    _uiState.value = StoryUiState.Error(e.toUserFriendlyMessage())
                }
            } catch (e: Exception) {

                _uiState.value = StoryUiState.Error(e.toUserFriendlyMessage())
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
                    _uiState.value = StoryUiState.Error("Satua ei löytynyt.")
                }
            } catch (e: Exception) {
                _uiState.value = StoryUiState.Error(e.toUserFriendlyMessage())
            }
        }
    }

    fun resetState() {
        _uiState.value = StoryUiState.Idle
    }
}