package fi.antero.satumaa.viewmodel.letter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fi.antero.satumaa.data.repository.LetterRepository
import fi.antero.satumaa.util.mapErrorToUserMessage
import fi.antero.satumaa.util.toUserFriendlyMessage
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
    val uiState: StateFlow<LetterUiState> = _uiState.asStateFlow()

    init {
        repo.getLetters()
            .onEach { letters ->
                val latestLetter = letters.firstOrNull()
                val currentUiStatus = _uiState.value.status

                if (_uiState.value.isViewMode) return@onEach

                if (latestLetter != null) {
                    // Estetään vanhan valmiin kirjeen lataus "Uusi kirje" -näkymään
                    val isDraftMode = currentUiStatus == null
                    val isOldFinishedLetter = latestLetter.status == "replied" || latestLetter.status == "error"

                    if (isDraftMode && isOldFinishedLetter) {
                        return@onEach
                    }

                    val shouldUpdate = if (latestLetter.status == "replying") {
                        true
                    } else if (latestLetter.status == "replied") {
                        currentUiStatus == "replying"
                    } else if (latestLetter.status == "error") {
                        currentUiStatus == "replying"
                    } else {
                        false
                    }

                    if (shouldUpdate) {
                        val shouldNotify = (currentUiStatus != "replied" && latestLetter.status == "replied")

                        if (latestLetter.status == "error") {
                            _uiState.update { state ->
                                state.copy(
                                    status = "error",
                                    isSending = false,
                                    // KÄÄNNETÄÄN BACKENDIN VIRHEKOODI SUOMEKSI
                                    error = latestLetter.errorMessage.mapErrorToUserMessage(),
                                    sentText = latestLetter.letterText
                                )
                            }
                        } else {
                            _uiState.update { state ->
                                state.copy(
                                    status = latestLetter.status,
                                    replyText = latestLetter.replyText,
                                    sentText = latestLetter.letterText,
                                    error = null,
                                    showReplyArrived = shouldNotify
                                )
                            }
                        }
                    }

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
                        isViewMode = true
                    )
                }
            }
        }
    }

    fun resetToNewLetter() {
        _uiState.update { current ->
            LetterUiState(
                userLocation = current.userLocation,
                distanceToSantaKm = current.distanceToSantaKm,
                hasLocationPermission = current.hasLocationPermission,
                isLocating = current.isLocating,
                text = "",
                status = null,
                sentText = "",
                replyText = null,
                isViewMode = false,
                error = null
            )
        }
    }

    fun onTextChange(text: String) {
        _uiState.update { it.copy(text = text, error = null) }
    }

    fun sendLetter(childName: String, onSuccess: () -> Unit) {
        val content = uiState.value.text.trim()
        if (content.isEmpty()) {
            _uiState.update { it.copy(error = "Kirjoita kirje ennen lähettämistä") }
            return
        }

        _uiState.update {
            it.copy(
                isSending = true,
                status = "replying",
                error = null,
                replyText = null,
                isViewMode = false,
                showReplyArrived = false
            )
        }

        viewModelScope.launch {
            val result = repo.sendLetter(content, childName)

            result.onSuccess {
                _uiState.update { it.copy(text = "", isSending = false, usedOfflineDemo = false) }
                repo.refreshLetters()
                onSuccess()
            }

            result.onFailure { e ->
                _uiState.update {
                    it.copy(
                        isSending = false,
                        status = null, // Pidetään muokkaustilassa
                        // KÄÄNNETÄÄN REPOSITORYN VIRHEKOODI SUOMEKSI
                        error = e.toUserFriendlyMessage()
                    )
                }
            }
        }
    }

    private fun refresh() {
        viewModelScope.launch { repo.refreshLetters() }
    }

    fun consumeReplyArrived() {
        _uiState.update { it.copy(showReplyArrived = false) }
    }
}