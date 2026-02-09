package fi.antero.satumaa.ui.viewmodel.story

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import fi.antero.satumaa.data.model.Story
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
 * StoryViewModel hallinnoi yksittäiseen satuun liittyvää logiikkaa.
 *
 * Käyttötapaukset:
 * 1. Uuden sadun luonti tekoälyllä (Generate).
 * 2. Generoidun sadun esikatselu ja tallennus (Save).
 * 3. Olemassa olevan sadun avaaminen luettavaksi (Load).
 */
@HiltViewModel
class StoryViewModel @Inject constructor(
    private val repository: StoryRepository,
    // Injektoidaan Application Context, jotta voimme kääntää virheilmoitukset
    // (R.string...) suoraan ViewModelissa ErrorUtilsin avulla.
    @ApplicationContext private val context: Context
) : ViewModel() {

    // UI-tila (Idle, Loading, Success, Error).
    // Käytämme StateFlowta, jotta Compose-näkymä päivittyy reaktiivisesti.
    private val _uiState = MutableStateFlow<StoryUiState>(StoryUiState.Idle)
    val uiState: StateFlow<StoryUiState> = _uiState.asStateFlow()

    /**
     * Käynnistää sadun luonnin tekoälyllä.
     *
     * @param childName Lapsen nimi.
     * @param word1-3 Taikasanat.
     * @param length Sadun pituus.
     * @param style Sadun tyyli.
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
            // 1. Siivotaan syötteet (poistetaan tyhjät välilyönnit)
            val keywords = listOf(word1, word2, word3).map { it.trim() }.filter { it.isNotEmpty() }

            // 2. Validointi: Vaaditaan vähintään yksi taikasana
            if (keywords.isEmpty()) {
                _uiState.value = StoryUiState.Error(Exception("STORY_KEYWORDS_EMPTY").toUserFriendlyMessage(context))
                return@launch
            }

            _uiState.value = StoryUiState.Loading

            // 3. Kutsutaan repositorya (Cloud Functions)
            val result = repository.generateStoryPreview(
                childName = childName,
                keywords = keywords,
                length = length.apiValue,
                style = style.apiValue
            )

            // 4. Käsitellään tulos
            result.onSuccess { previewStory ->
                // Onnistui: Näytetään esikatselu (Success-tila)
                _uiState.value = StoryUiState.Success(previewStory)
            }.onFailure { e ->
                // Epäonnistui: Käännetään virhe suomeksi ja näytetään käyttäjälle
                _uiState.value = StoryUiState.Error(e.toUserFriendlyMessage(context))
            }
        }
    }

    /**
     * Tallentaa nykyisen esikatselussa olevan sadun tietokantaan.
     */
    fun saveCurrentStory() {
        val currentState = _uiState.value

        // Varmistetaan, että meillä on onnistuneesti generoitu satu
        if (currentState is StoryUiState.Success) {
            val storyToSave = currentState.story

            // Estetään tuplatallennus: Jos sadulla on jo ID, se on jo tallennettu.
            if (storyToSave.id.isNotEmpty()) return

            viewModelScope.launch {
                val result = repository.saveStory(storyToSave)

                result.onSuccess { newId ->
                    // Päivitetään UI-tilaan tallennettu versio (jolla on nyt ID)
                    val savedStory = storyToSave.copy(id = newId)
                    _uiState.value = StoryUiState.Success(savedStory)
                }.onFailure { e ->
                    _uiState.value = StoryUiState.Error(e.toUserFriendlyMessage(context))
                }
            }
        }
    }

    /**
     * Hakee yksittäisen sadun katselua varten (esim. kun listasta klikataan).
     */
    fun loadStory(storyId: String) {
        viewModelScope.launch {
            _uiState.value = StoryUiState.Loading
            try {
                val story = repository.getStory(storyId)
                if (story != null) {
                    _uiState.value = StoryUiState.Success(story)
                } else {
                    _uiState.value = StoryUiState.Error(Exception("STORY_NOT_FOUND").toUserFriendlyMessage(context))
                }
            } catch (e: Exception) {
                _uiState.value = StoryUiState.Error(e.toUserFriendlyMessage(context))
            }
        }
    }

    /**
     * Palauttaa tilan alkutilaan (esim. "Luo uusi satu" -näkymään palatessa).
     */
    fun resetState() {
        _uiState.value = StoryUiState.Idle
    }
}