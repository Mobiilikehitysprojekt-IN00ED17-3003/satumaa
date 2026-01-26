package fi.antero.satumaa.viewmodel.letter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import fi.antero.satumaa.data.repository.LetterRepository
import fi.antero.satumaa.data.repository.LetterRepositoryImpl
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// UI-tila kirjeen lähetykselle ja vastaukselle
data class LetterUiState(
    val text: String = "",
    val isSending: Boolean = false,
    val status: String? = null, // replying | replied | error
    val replyText: String? = null,
    val error: String? = null,
    val usedOfflineDemo: Boolean = false // Kertoo jos ollaan demo-moodissa
)

class LetterViewModel(
    private val repo: LetterRepository = LetterRepositoryImpl(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) : ViewModel() {

    private val _uiState = MutableStateFlow(LetterUiState())
    val uiState: StateFlow<LetterUiState> = _uiState

    private var listener: ListenerRegistration? = null

    fun onTextChange(text: String) {
        _uiState.update { it.copy(text = text, error = null) }
    }

    fun sendLetter() {
        val content = uiState.value.text.trim()
        if (content.isEmpty()) {
            _uiState.update { it.copy(error = "Kirjoita kirje ennen lähettämistä") }
            return
        }

        // Asetetaan tila odottamaan vastausta
        _uiState.update { 
            it.copy(isSending = true, status = "replying", error = null, replyText = null) 
        }

        // Lähetys yritys backendiin
        repo.sendLetter(content)
            .addOnSuccessListener { doc ->
                _uiState.update { it.copy(text = "", isSending = false, usedOfflineDemo = false) }
                listenToLetter(doc.id)
            }
            .addOnFailureListener { e ->
                // Jos backend ei vastaa, siirrytään demo-vastaussimulaatioon
                startOfflineDemoReply(
                    originalError = e.message ?: "Backend ei käytössä / oikeudet puuttuvat"
                )
            }
    }

    /**
     * Mahdollistaa vastauksen simuloinnin esim. testi-napista
     */
    fun simulateReply() {
        startOfflineDemoReply(originalError = null)
    }

    private fun startOfflineDemoReply(originalError: String?) {
        listener?.remove()
        listener = null

        _uiState.update {
            it.copy(
                isSending = false,
                status = "replying",
                usedOfflineDemo = true,
                error = originalError 
            )
        }

        // Simuloidaan pukin vastausviive
        viewModelScope.launch {
            delay(1200) 
            _uiState.update { state ->
                state.copy(
                    status = "replied",
                    replyText = "Ho ho ho! Kiitos kirjeestäsi 🎅🎁\nTerveisin, Joulupukki",
                    error = null 
                )
            }
        }
    }

    private fun listenToLetter(id: String) {
        listener?.remove()
        listener = db.collection("letters")
            .document(id)
            .addSnapshotListener { snap, err ->
                if (err != null || snap == null) return@addSnapshotListener

                _uiState.update {
                    it.copy(
                        status = snap.getString("status"),
                        replyText = snap.getString("replyText"),
                        error = null
                    )
                }
            }
    }

    override fun onCleared() {
        listener?.remove()
        super.onCleared()
    }
}