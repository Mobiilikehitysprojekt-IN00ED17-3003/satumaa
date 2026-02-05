package fi.antero.satumaa.viewmodel.letter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fi.antero.satumaa.data.model.Letter
import fi.antero.satumaa.data.repository.LetterRepository
import fi.antero.satumaa.data.repository.LocationRepository
import fi.antero.satumaa.util.MathChallengeGenerator
import fi.antero.satumaa.util.TravelTimeCalculator
import fi.antero.satumaa.util.mapErrorToUserMessage
import fi.antero.satumaa.util.toUserFriendlyMessage
import kotlinx.coroutines.Job
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
    private val repo: LetterRepository,
    private val locationRepo: LocationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LetterUiState())
    val uiState: StateFlow<LetterUiState> = _uiState.asStateFlow()

    private val locallyOpenedLetterIds = mutableSetOf<String>()

    private var activeDeliveryTime: Long? = null

    private var pendingActiveLetterId: String? = null

    private var locationJob: Job? = null

    init {
        repo.getLetters()
            .onEach { letters ->
                handleLettersUpdate(letters)
            }
            .launchIn(viewModelScope)

        viewModelScope.launch {
            while (true) {
                delay(1000)
                checkTimeAndRefreshIfNeeded()
            }
        }

        refresh()
    }

    private fun handleLettersUpdate(letters: List<Letter>) {
        val sorted = letters.sortedByDescending { it.createdAt?.toDate()?.time ?: 0L }

        val currentId = _uiState.value.currentLetterId
        if (currentId != null) {
            val target = sorted.find { it.id == currentId }
            if (target != null) {
                val isOpened = target.isOpened || locallyOpenedLetterIds.contains(target.id)
                if (isOpened != _uiState.value.isOpened) {
                    _uiState.update { it.copy(isOpened = isOpened) }
                }
            }
        }

        if (_uiState.value.isViewMode) return
        if (_uiState.value.isNewLetterMode) return

        val pendingId = pendingActiveLetterId
        if (pendingId != null && _uiState.value.currentLetterId == pendingId) {
            val found = sorted.find { it.id == pendingId }
            if (found == null) return
            pendingActiveLetterId = null
        }

        val pinned = currentId?.let { id ->
            sorted.find { it.id == id }?.takeIf {
                !(it.isOpened || locallyOpenedLetterIds.contains(it.id))
            }
        }

        val activeCandidate = pinned ?: sorted.firstOrNull {
            !(it.isOpened || locallyOpenedLetterIds.contains(it.id))
        }

        if (activeCandidate == null) {
            beginNewLetter()
            return
        }

        val createdAtMs = activeCandidate.createdAt?.toDate()?.time

        if (_uiState.value.currentLetterId != activeCandidate.id ||
            _uiState.value.currentLetterCreatedAtMs != createdAtMs ||
            _uiState.value.isNewLetterMode
        ) {
            _uiState.update {
                it.copy(
                    currentLetterId = activeCandidate.id,
                    currentLetterCreatedAtMs = createdAtMs,
                    isNewLetterMode = false
                )
            }
        }

        if (activeDeliveryTime == null && activeCandidate.status == "replying") {
            val startMs = createdAtMs ?: System.currentTimeMillis()
            activeDeliveryTime = TravelTimeCalculator.getDeliveryTime(activeCandidate.id, startMs)
        }

        processLetterState(activeCandidate)
    }

    private fun checkTimeAndRefreshIfNeeded() {
        val endTime = activeDeliveryTime ?: return
        val now = System.currentTimeMillis()

        if (now >= endTime) {
            val currentStatus = _uiState.value.status
            val currentText = _uiState.value.replyText

            if (currentStatus == "replying" || currentText.isNullOrBlank()) {
                refresh()
            }

            val currentId = _uiState.value.currentLetterId ?: return
            viewModelScope.launch {
                val letter = repo.getLetterById(currentId)
                if (letter != null) {
                    processLetterState(letter)
                }
            }
        }
    }

    private fun processLetterState(letter: Letter) {
        val isOpened = letter.isOpened || locallyOpenedLetterIds.contains(letter.id)
        val endTime = activeDeliveryTime
        val now = System.currentTimeMillis()

        var finalStatus = letter.status
        var showArrived = false

        if (letter.status == "replied" && !isOpened) {
            val isTimeUp = endTime != null && now >= endTime

            if (!isTimeUp && endTime != null) {
                finalStatus = "replying"
            } else {
                finalStatus = "replied"
                if (_uiState.value.status != "replied") {
                    showArrived = true
                }
            }
        } else if (letter.status == "replied") {
            showArrived = false
        }

        _uiState.update {
            it.copy(
                status = finalStatus,
                replyText = letter.replyText,
                sentText = letter.letterText,
                isSending = false,
                error = if (letter.status == "error") letter.errorMessage.mapErrorToUserMessage() else null,
                showReplyArrived = if (finalStatus == "replying") false else (it.showReplyArrived || showArrived),
                isOpened = isOpened,
                isNewLetterMode = false,
                currentLetterCreatedAtMs = it.currentLetterCreatedAtMs ?: letter.createdAt?.toDate()?.time
            )
        }
    }

    fun onFlowEntered(hasPermission: Boolean, isLocationEnabled: Boolean) {
        _uiState.update {
            it.copy(
                hasLocationPermission = hasPermission,
                isLocationEnabled = isLocationEnabled,
                locationError = null
            )
        }
        ensureLocationOnce()
    }

    fun requestLocationNow() {
        ensureLocationOnce(force = true)
    }

    private fun ensureLocationOnce(force: Boolean = false) {
        val s = _uiState.value
        if (!s.hasLocationPermission) return
        if (!s.isLocationEnabled) return
        if (!force && s.userLocation != null) return
        if (locationJob?.isActive == true) return

        _uiState.update { it.copy(isLocating = true, locationError = null) }

        locationJob = viewModelScope.launch {
            runCatching {
                val loc = locationRepo.getSingleLocation()
                if (loc != null) {
                    _uiState.update { it.copy(userLocation = loc, isLocating = false, locationError = null) }
                } else {
                    _uiState.update { it.copy(isLocating = false, locationError = "LOCATION_ERROR") }
                }
            }.onFailure {
                _uiState.update { it.copy(isLocating = false, locationError = "LOCATION_ERROR") }
            }
        }
    }

    fun beginNewLetter() {
        pendingActiveLetterId = null
        activeDeliveryTime = null
        _uiState.update { current ->
            LetterUiState(
                hasLocationPermission = current.hasLocationPermission,
                isLocationEnabled = current.isLocationEnabled,
                userLocation = current.userLocation,
                isLocating = current.isLocating,
                locationError = current.locationError,
                isNewLetterMode = true
            )
        }
    }

    fun exitViewMode() {
        if (_uiState.value.isViewMode) {
            _uiState.update { it.copy(isViewMode = false) }
        }
    }

    fun loadLetter(id: String) {
        viewModelScope.launch {
            val letter = repo.getLetterById(id)
            if (letter != null) {
                activeDeliveryTime = null
                pendingActiveLetterId = null
                _uiState.update {
                    it.copy(
                        currentLetterId = letter.id,
                        currentLetterCreatedAtMs = letter.createdAt?.toDate()?.time,
                        isViewMode = true,
                        isNewLetterMode = false
                    )
                }
                processLetterState(letter)
            }
        }
    }

    fun setActiveLetter(letterId: String) {
        if (_uiState.value.currentLetterId == letterId && !_uiState.value.isNewLetterMode) return

        pendingActiveLetterId = letterId
        _uiState.update {
            it.copy(
                currentLetterId = letterId,
                isViewMode = false,
                isNewLetterMode = false
            )
        }

        viewModelScope.launch {
            val letter = repo.getLetterById(letterId)
            if (letter != null) {
                _uiState.update { it.copy(currentLetterCreatedAtMs = letter.createdAt?.toDate()?.time) }
                if (activeDeliveryTime == null && letter.status == "replying") {
                    val startMs = letter.createdAt?.toDate()?.time ?: System.currentTimeMillis()
                    activeDeliveryTime = TravelTimeCalculator.getDeliveryTime(letter.id, startMs)
                }
                processLetterState(letter)
                pendingActiveLetterId = null
            }
        }
    }

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

    fun submitMathAnswer(answer: String) {
        val challenge = _uiState.value.mathChallenge ?: return
        if (answer.trim().toIntOrNull() == challenge.correctAnswer) {
            dismissMathChallenge()

            val id = _uiState.value.currentLetterId ?: return
            _uiState.update { it.copy(isViewMode = true, isNewLetterMode = false) }

            loadLetter(id)
            markLetterAsOpened()
        } else {
            _uiState.update { it.copy(mathError = true) }
        }
    }

    fun markLetterAsOpened() {
        val id = uiState.value.currentLetterId ?: return
        _uiState.update { it.copy(isOpened = true) }
        locallyOpenedLetterIds.add(id)
        activeDeliveryTime = null
        viewModelScope.launch { repo.markAsOpened(id) }
    }

    fun onTextChange(text: String) {
        _uiState.update { it.copy(text = text, error = null) }
    }

    fun sendLetter(childName: String, onSuccess: (String) -> Unit) {
        val content = uiState.value.text.trim()
        if (content.isEmpty()) return

        val startMs = System.currentTimeMillis()

        _uiState.update {
            it.copy(
                isSending = true,
                status = "replying",
                error = null,
                isNewLetterMode = false
            )
        }

        viewModelScope.launch {
            val result = repo.sendLetter(content, childName)
            result.onSuccess { id ->
                pendingActiveLetterId = id
                activeDeliveryTime = TravelTimeCalculator.getDeliveryTime(id, startMs)

                _uiState.update {
                    it.copy(
                        text = "",
                        isSending = false,
                        currentLetterId = id,
                        currentLetterCreatedAtMs = startMs,
                        isViewMode = false,
                        isNewLetterMode = false
                    )
                }

                refresh()
                onSuccess(id)
            }

            result.onFailure { e ->
                _uiState.update { it.copy(isSending = false, error = e.toUserFriendlyMessage()) }
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