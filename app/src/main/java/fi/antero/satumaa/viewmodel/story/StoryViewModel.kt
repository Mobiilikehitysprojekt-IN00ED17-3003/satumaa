package fi.antero.satumaa.ui.viewmodel.story

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fi.antero.satumaa.data.local.entity.StoryEntity
import fi.antero.satumaa.repository.StoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// Yksinkertainen tila tälle yhdelle ruudulle
sealed interface StoryScreenState {
    data object Idle : StoryScreenState
    data object Loading : StoryScreenState
    data class Success(val story: StoryEntity) : StoryScreenState
    data class Error(val message: String) : StoryScreenState
}

@HiltViewModel
class StoryViewModel @Inject constructor(
    private val repository: StoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<StoryScreenState>(StoryScreenState.Idle)
    val uiState: StateFlow<StoryScreenState> = _uiState.asStateFlow()

    // Otetaan vastaan lapsen nimi ja 3 taikasanaa
    fun generateStory(
        childName: String,
        word1: String,
        word2: String,
        word3: String
    ) {
        viewModelScope.launch {
            // Yhdistetään sanat ja siivotaan tyhjät pois
            val keywords = listOf(word1, word2, word3)
                .map { it.trim() }
                .filter { it.isNotEmpty() }

            if (keywords.isEmpty()) {
                _uiState.value = StoryScreenState.Error("Kirjoita ainakin yksi taikasana!")
                return@launch
            }

            _uiState.value = StoryScreenState.Loading

            // Kutsutaan repositorya (oletusarvoilla NORMAL pituus ja FUNNY tyyli)
            val result = repository.generateAndSaveStory(
                childName = childName,
                keywords = keywords,
                length = "NORMAL",
                style = "FUNNY"
            )

            result.onSuccess { storyId ->
                // Kun ID on saatu, haetaan heti koko satu näytettäväksi
                val fullStory = repository.getStory(storyId)
                if (fullStory != null) {
                    _uiState.value = StoryScreenState.Success(fullStory)
                } else {
                    _uiState.value = StoryScreenState.Error("Satua ei löytynyt tietokannasta.")
                }
            }.onFailure { e ->
                _uiState.value = StoryScreenState.Error(e.localizedMessage ?: "Virhe sadun luonnissa")
            }
        }
    }

    fun resetState() {
        _uiState.value = StoryScreenState.Idle
    }
}