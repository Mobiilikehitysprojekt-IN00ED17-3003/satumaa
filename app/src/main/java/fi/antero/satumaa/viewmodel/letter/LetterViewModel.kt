package fi.antero.satumaa.viewmodel.letter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fi.antero.satumaa.data.repository.LetterRepository
import fi.antero.satumaa.util.toUserFriendlyMessage
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LetterViewModel @Inject constructor(
    private val repo: LetterRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LetterUiState())
    val uiState: StateFlow<LetterUiState> = _uiState

    init {
        // Kuunnellaan listaa automaattisesti
        repo.getLetters()
            .onEach { letters ->
                val latestLetter = letters.firstOrNull()


                if (latestLetter != null && !_uiState.value.isViewMode) {

                    val shouldUpdate = if (latestLetter.status == "replying") {
                        true // Aina näytetään, jos vastaus on kesken
                    } else if (latestLetter.status == "replied") {
                        // Näytetään valmis kirje vain, jos käyttäjä oli juuri odottamassa sitä
                        _uiState.value.status == "replying"
                    } else {
                        false
                    }

                    if (shouldUpdate) {
                        _uiState.update { state ->
                            state.copy(
                                status = latestLetter.status,
                                replyText = latestLetter.replyText,
                                sentText = latestLetter.letterText,
                                error = null
                            )
                        }
                    }

                    // Käynnistetään pollaus vain jos vastausta odotetaan
                    if (latestLetter.status == "replying") {
                        delay(4000)
                        repo.refreshLetters()
                    }
                }
            }
            .launchIn(viewModelScope)

        refresh()
    }

    fun loadLetter(id: String) {
        viewModelScope.launch {
            val letter = repo.getLetterById(id)
            if (letter != null) {
                _uiState.update {
                    it.copy(
                        status = letter.status,
                        text = "",
                        sentText = letter.letterText,
                        replyText = letter.replyText,
                        isSending = false,
                        error = null,
                        isViewMode = true // Estää automaattipäivitykset
                    )
                }
            }
        }
    }

    fun resetToNewLetter() {
        _uiState.update {
            LetterUiState(
                isViewMode = false,
                status = null,
                text = "",
                sentText = "",
                replyText = null
            )
        }
    }

    fun onTextChange(text: String) {
        _uiState.update { it.copy(text = text, error = null) }
    }

    fun sendLetter(childName: String) {
        val content = uiState.value.text.trim()
        if (content.isEmpty()) {
            _uiState.update { it.copy(error = "Kirjoita kirje ennen lähettämistä") }
            return
        }

        _uiState.update {
            it.copy(isSending = true, status = "replying", error = null, replyText = null, isViewMode = false)
        }

        viewModelScope.launch {
            val result = repo.sendLetter(content, childName)
            result.onSuccess {
                _uiState.update { it.copy(text = "", isSending = false, usedOfflineDemo = false) }
                repo.refreshLetters()
            }.onFailure { e ->
                startOfflineDemoReply(e.toUserFriendlyMessage())
            }
        }
    }

    private fun refresh() {
        viewModelScope.launch { repo.refreshLetters() }
    }

    fun simulateReply() {
        startOfflineDemoReply(null)
    }

    private fun startOfflineDemoReply(originalError: String?) {
        _uiState.update {
            it.copy(
                isSending = false,
                status = "replying",
                usedOfflineDemo = true,
                error = originalError
            )
        }
        viewModelScope.launch {
            delay(2000)
            _uiState.update { state ->
                state.copy(
                    status = "replied",
                    replyText = "Ho ho ho! Kiitos kirjeestäsi 🎅🎁\nTerveisin, Joulupukki (Offline-tila)",
                    error = null
                )
            }
        }
    }
}