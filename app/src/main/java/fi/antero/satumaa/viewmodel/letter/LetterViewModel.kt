package fi.antero.satumaa.viewmodel.letter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fi.antero.satumaa.data.model.Letter
import fi.antero.satumaa.data.repository.LetterRepository
import fi.antero.satumaa.util.MathChallengeGenerator
import fi.antero.satumaa.util.TravelTimeCalculator
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

    private val locallyOpenedLetterIds = mutableSetOf<String>()

    // Pidämme kirjaa, milloin matka päättyy (Absoluuttinen aika)
    private var activeDeliveryTime: Long? = null

    init {
        // 1. Tietokantakuuntelija
        repo.getLetters()
            .onEach { letters ->
                val currentId = _uiState.value.currentLetterId

                // Päivitetään avaus-tila
                if (currentId != null) {
                    val target = letters.find { it.id == currentId }
                    if (target != null) {
                        val isOpened = target.isOpened || locallyOpenedLetterIds.contains(target.id)
                        if (isOpened != _uiState.value.isOpened) {
                            _uiState.update { it.copy(isOpened = isOpened) }
                        }
                    }
                }

                if (_uiState.value.isViewMode) return@onEach

                val latestLetter = letters.firstOrNull()
                if (latestLetter != null) {
                    // Asetetaan ID jos puuttuu
                    if (_uiState.value.currentLetterId == null) {
                        _uiState.update { it.copy(currentLetterId = latestLetter.id) }
                    }

                    // Lasketaan matka-aika kerran, jos sitä ei ole
                    if (activeDeliveryTime == null && latestLetter.status == "replying") {
                        val createdAt = latestLetter.createdAt?.toDate()?.time ?: System.currentTimeMillis()
                        activeDeliveryTime = TravelTimeCalculator.getDeliveryTime(latestLetter.id, createdAt)
                    }

                    // Prosessoidaan tila
                    if (_uiState.value.currentLetterId == latestLetter.id) {
                        processLetterState(latestLetter)
                    }
                }
            }
            .launchIn(viewModelScope)

        // 2. Sydämenlyönti: Tarkistaa kelloa ja pakottaa päivityksen jos aika on täysi
        // mutta data puuttuu.
        viewModelScope.launch {
            while (true) {
                delay(1000)
                checkTimeAndRefreshIfNeeded()
            }
        }

        refresh()
    }

    private fun checkTimeAndRefreshIfNeeded() {
        val endTime = activeDeliveryTime ?: return
        val now = System.currentTimeMillis()

        // Jos aika on kulunut umpeen...
        if (now >= endTime) {
            val currentStatus = _uiState.value.status
            val currentText = _uiState.value.replyText

            // Jos status on yhä replying TAI status on replied mutta teksti puuttuu
            // -> Pakotetaan haku palvelimelta
            if (currentStatus == "replying" || currentText.isNullOrBlank()) {
                refresh()
            }

            // Päivitetään UI:n tila heti ajan perusteella
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

        // LOGIIKKA: "AIKA ON YLIN AUKTORITEETTI"

        if (letter.status == "replied" && !isOpened) {
            val isTimeUp = endTime != null && now >= endTime

            if (!isTimeUp) {
                // Matka on kesken (vaikka data olisi tullut)
                finalStatus = "replying"
            } else {
                // Matka on ohi -> Tila on AINA replied.
                finalStatus = "replied"

                // Jos teksti puuttuu, varmistetaan että refresh pyörii (tehty checkTimeAndRefreshIfNeeded-funktiossa)
                // Mutta emme enää pakota statusta takaisin "replying"-tilaan!

                if (_uiState.value.status != "replied") {
                    showArrived = true
                }
            }
        } else if (letter.status == "replied") {
            // Avattu
            showArrived = false
        }

        _uiState.update {
            it.copy(
                status = finalStatus,
                replyText = letter.replyText, // Voi olla null, UI hoitaa sen
                sentText = letter.letterText,
                isSending = false,
                error = if (letter.status == "error") letter.errorMessage.mapErrorToUserMessage() else null,
                showReplyArrived = if (finalStatus == "replying") false else (it.showReplyArrived || showArrived),
                isOpened = isOpened
            )
        }
    }

    // --- Vakiometodit ---
    fun showMathChallenge() { _uiState.update { it.copy(isMathDialogVisible = true, mathChallenge = MathChallengeGenerator.generateChallenge(), mathError = false) } }
    fun dismissMathChallenge() { _uiState.update { it.copy(isMathDialogVisible = false, mathError = false) } }
    fun submitMathAnswer(answer: String) {
        val challenge = _uiState.value.mathChallenge ?: return
        if (answer.trim().toIntOrNull() == challenge.correctAnswer) { dismissMathChallenge(); markLetterAsOpened() }
        else { _uiState.update { it.copy(mathError = true) } }
    }
    fun markLetterAsOpened() {
        val id = uiState.value.currentLetterId ?: return
        _uiState.update { it.copy(isOpened = true) }
        locallyOpenedLetterIds.add(id)
        activeDeliveryTime = null
        viewModelScope.launch { repo.markAsOpened(id) }
    }
    fun loadLetter(id: String) {
        viewModelScope.launch {
            val letter = repo.getLetterById(id)
            if (letter != null) {
                _uiState.update { it.copy(currentLetterId = letter.id, isViewMode = true) }
                processLetterState(letter)
            }
        }
    }
    fun resetToNewLetter() {
        activeDeliveryTime = null
        _uiState.update { LetterUiState() }
    }
    fun onTextChange(text: String) { _uiState.update { it.copy(text = text, error = null) } }
    fun sendLetter(childName: String, onSuccess: (String) -> Unit) {
        val content = uiState.value.text.trim()
        if (content.isEmpty()) return
        _uiState.update { it.copy(isSending = true, status = "replying") }
        viewModelScope.launch {
            val result = repo.sendLetter(content, childName)
            result.onSuccess { id ->
                val deliveryTime = TravelTimeCalculator.getDeliveryTime(id, System.currentTimeMillis())
                activeDeliveryTime = deliveryTime
                _uiState.update { it.copy(text = "", isSending = false) }
                refresh()
                onSuccess(id)
            }
            result.onFailure { e -> _uiState.update { it.copy(isSending = false, error = e.toUserFriendlyMessage()) } }
        }
    }
    private fun refresh() { viewModelScope.launch { repo.refreshLetters() } }
    fun consumeReplyArrived() { _uiState.update { it.copy(showReplyArrived = false) } }
}
