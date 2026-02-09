package fi.antero.satumaa.ui.viewmodel.story

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fi.antero.satumaa.data.repository.StoryRepository
import fi.antero.satumaa.ui.components.story.create.StoryLength
import fi.antero.satumaa.ui.components.story.create.StoryStyle
import fi.antero.satumaa.util.toUserFriendlyMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * StoryViewModel hallinnoi sadun luomiseen, tallentamiseen ja hakemiseen liittyvää logiikkaa.
 * Se toimii välikätenä UI:n ja Repositoryn välillä.
 */
@HiltViewModel
class StoryViewModel @Inject constructor(
    private val repository: StoryRepository
) : ViewModel() {

    // UI-tila (Idle, Loading, Success, Error)
    private val _uiState = MutableStateFlow<StoryUiState>(StoryUiState.Idle)
    val uiState: StateFlow<StoryUiState> = _uiState.asStateFlow()

    /**
     * Käynnistää sadun luonnin tekoälyllä.
     *
     * @param childName Lapsen nimi (tai kenelle satu luodaan).
     * @param word1-3 Taikasanat.
     * @param length Sadun pituus (Enum).
     * @param style Sadun tyyli (Enum).
     */
    fun generateStory(
        childName: String,
        word1: String,
        word2: String,
        word3: String,
        length: StoryLength,
        style: StoryStyle
    ) {
        viewModelScope.launch {
            // Siistitään syötteet
            val keywords = listOf(word1, word2, word3).map { it.trim() }.filter { it.isNotEmpty() }

            // Tarkistetaan, onko avainsanoja
            if (keywords.isEmpty()) {
                // Käytetään teknistä virhekoodia, jonka ErrorUtils kääntää käyttäjäystävälliseksi
                _uiState.value = StoryUiState.Error(Exception("STORY_KEYWORDS_EMPTY").toUserFriendlyMessage())
                return@launch
            }

            _uiState.value = StoryUiState.Loading

            // Kutsutaan repositorya (käytetään Enumin apiValue-kenttää)
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

    /**
     * Tallentaa nykyisen esikatselussa olevan sadun tietokantaan.
     * Jos satu on jo tallennettu (sillä on ID), toimintoa ei suoriteta uudestaan.
     */
    fun saveCurrentStory() {
        val currentState = _uiState.value
        if (currentState is StoryUiState.Success) {
            val storyToSave = currentState.story

            // Estetään duplikaattitallennus
            if (storyToSave.id.isNotEmpty()) return

            viewModelScope.launch {
                val result = repository.saveStory(storyToSave)

                result.onSuccess { newId ->
                    // Päivitetään tilaan tallennettu satu uudella ID:llä
                    val savedStory = storyToSave.copy(id = newId)
                    _uiState.value = StoryUiState.Success(savedStory)
                }.onFailure { e ->
                    _uiState.value = StoryUiState.Error(e.toUserFriendlyMessage())
                }
            }
        }
    }

    /**
     * Hakee yksittäisen sadun katselua varten (esim. listasta klikatessa).
     */
    fun loadStory(storyId: String) {
        viewModelScope.launch {
            _uiState.value = StoryUiState.Loading
            try {
                val story = repository.getStory(storyId)
                if (story != null) {
                    _uiState.value = StoryUiState.Success(story)
                } else {
                    _uiState.value = StoryUiState.Error(Exception("STORY_NOT_FOUND").toUserFriendlyMessage())
                }
            } catch (e: Exception) {
                _uiState.value = StoryUiState.Error(e.toUserFriendlyMessage())
            }
        }
    }

    /**
     * Palauttaa tilan alkutilaan (esim. kun halutaan luoda uusi satu).
     */
    fun resetState() {
        _uiState.value = StoryUiState.Idle
    }
}