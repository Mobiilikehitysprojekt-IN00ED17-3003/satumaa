package fi.antero.satumaa.viewmodel.letter

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import fi.antero.satumaa.data.repository.LetterRepository
import fi.antero.satumaa.notifications.NotificationHelper
import fi.antero.satumaa.util.MathChallengeGenerator
import fi.antero.satumaa.util.mapErrorToUserMessage
import fi.antero.satumaa.util.toUserFriendlyMessage
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LetterViewModel @Inject constructor(
    private val repo: LetterRepository,
    @ApplicationContext private val appContext: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(LetterUiState())
    val uiState: StateFlow<LetterUiState> = _uiState.asStateFlow()

    // Pidämme kirjaa tässä sessiossa avatuista kirjeistä varmuuden vuoksi
    private val locallyOpenedLetterIds = mutableSetOf<String>()

    init {
        repo.getLetters()
            .onEach { letters ->
                val currentUiState = _uiState.value
                val currentId = currentUiState.currentLetterId
                val isViewMode = currentUiState.isViewMode

                // 1. PÄIVITETÄÄN AINA NYKYISEN KIRJEEN TILA (isOpened)
                if (currentId != null) {
                    val targetLetter = letters.find { it.id == currentId }
                    if (targetLetter != null) {
                        val isOpened = targetLetter.isOpened || locallyOpenedLetterIds.contains(targetLetter.id)

                        if (isOpened != currentUiState.isOpened) {
                            _uiState.update { it.copy(isOpened = isOpened) }
                        }
                    }
                }

                // 2. JOS OLLAAN KATSELUTILASSA, LOPETETAAN TÄHÄN
                if (isViewMode) return@onEach

                // 3. NORMAALI LOGIIKKA (UUSIN KIRJE / DRAFT)
                val latestLetter = letters.firstOrNull()
                if (latestLetter != null) {
                    val currentUiStatus = currentUiState.status
                    val isDraftMode = currentUiStatus == null
                    val isOldFinishedLetter = latestLetter.status == "replied" || latestLetter.status == "error"

                    if (isDraftMode && isOldFinishedLetter) {
                        return@onEach
                    }

                    // Asetetaan ID ja tila
                    val isLetterOpened = latestLetter.isOpened || locallyOpenedLetterIds.contains(latestLetter.id)

                    if (currentId == null || currentId == latestLetter.id) {
                        _uiState.update { it.copy(currentLetterId = latestLetter.id) }
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
                        // ✅ notify vain kun tila vaihtuu replying -> replied
                        val shouldNotify = (currentUiStatus != "replied" && latestLetter.status == "replied")
                        if (shouldNotify) {
                            NotificationHelper.showSantaReplyNotification(appContext)
                        }

                        if (latestLetter.status == "error") {
                            _uiState.update { state ->
                                state.copy(
                                    status = "error",
                                    isSending = false,
                                    error = latestLetter.errorMessage.mapErrorToUserMessage(),
                                    sentText = latestLetter.letterText,
                                    isOpened = isLetterOpened
                                )
                            }
                        } else {
                            _uiState.update { state ->
                                state.copy(
                                    status = latestLetter.status,
                                    replyText = latestLetter.replyText,
                                    sentText = latestLetter.letterText,
                                    error = null,
                                    showReplyArrived = shouldNotify,
                                    isOpened = isLetterOpened
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

    // --- MATEMATIIKKA LOGIIKKA ---

    fun showMathChallenge() {
        _uiState.update {
            it.copy(
                isMathDialogVisible = true,
                mathChallenge = MathChallengeGenerator.generateChallenge(),
                mathError = false
            )
        }
    }

    fun dismissMathChallenge() {
        _uiState.update { it.copy(isMathDialogVisible = false, mathError = false) }
    }

    fun submitMathAnswer(answerString: String) {
        val challenge = _uiState.value.mathChallenge ?: return
        val userAnswer = answerString.trim().toIntOrNull()

        if (userAnswer == challenge.correctAnswer) {
            dismissMathChallenge()
            markLetterAsOpened()
        } else {
            _uiState.update { it.copy(mathError = true) }
        }
    }

    // ----------------------------

    fun markLetterAsOpened() {
        val currentId = uiState.value.currentLetterId ?: return

        _uiState.update { it.copy(isOpened = true) }
        locallyOpenedLetterIds.add(currentId)

        viewModelScope.launch {
            repo.markAsOpened(currentId)

            val updatedLetter = repo.getLetterById(currentId)
            if (updatedLetter != null && updatedLetter.isOpened) {
                _uiState.update { it.copy(isOpened = true) }
            }
        }
    }

    fun loadLetter(id: String) {
        viewModelScope.launch {
            val letter = repo.getLetterById(id)
            if (letter != null) {
                val isOpened = letter.isOpened || locallyOpenedLetterIds.contains(id)

                _uiState.update {
                    it.copy(
                        currentLetterId = letter.id,
                        status = letter.status,
                        text = "",
                        sentText = letter.letterText,
                        replyText = letter.replyText,
                        isSending = false,
                        error = null,
                        isViewMode = true,
                        isOpened = isOpened
                    )
                }
            }
        }
    }

    fun resetToNewLetter() {
        _uiState.update { current ->
            LetterUiState(
                currentLetterId = null,
                userLocation = current.userLocation,
                distanceToSantaKm = current.distanceToSantaKm,
                hasLocationPermission = current.hasLocationPermission,
                isLocating = current.isLocating,
                text = "",
                status = null,
                sentText = "",
                replyText = null,
                isViewMode = false,
                error = null,
                isOpened = false
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
                showReplyArrived = false,
                isOpened = false
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
                        status = null,
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
