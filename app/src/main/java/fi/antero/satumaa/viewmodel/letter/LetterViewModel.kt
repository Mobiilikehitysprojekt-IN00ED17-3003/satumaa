package fi.antero.satumaa.viewmodel

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

data class LetterUiState(
    val text: String = "",
    val isSending: Boolean = false,
    val status: String? = null, // replying | replied | error
    val replyText: String? = null,
    val error: String? = null,
    val usedOfflineDemo: Boolean = false // ✅ uusi: kerrotaan käytettiinkö demo-moodia
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

        // UI heti “lähettää/odottaa”
        _uiState.update { it.copy(isSending = true, status = "replying", error = null, replyText = null) }

        // Yritetään normaalisti backendin kautta
        repo.sendLetter(content)
            .addOnSuccessListener { doc ->
                _uiState.update { it.copy(text = "", isSending = false, usedOfflineDemo = false) }
                listenToLetter(doc.id)
            }
            .addOnFailureListener { e ->
                // ✅ Jos backend ei ole valmis / säännöt estää → mennään demo/offline-moodiin,
                // jotta AR-ominaisuutta voidaan kehittää ja demota.
                startOfflineDemoReply(
                    originalError = e.message ?: "Backend ei käytössä / oikeudet puuttuvat"
                )
            }
    }

    /**
     * Dev-nappi voi kutsua tätä suoraan.
     */
    fun simulateReply() {
        startOfflineDemoReply(originalError = null)
    }

    private fun startOfflineDemoReply(originalError: String?) {
        // Lopetetaan mahdollinen Firestore-kuuntelu
        listener?.remove()
        listener = null

        // Näytetään virheteksti, mutta annetaan silti edetä
        _uiState.update {
            it.copy(
                isSending = false,
                status = "replying",
                usedOfflineDemo = true,
                error = originalError // halutessa näytetään “backend puuttuu”, mutta flow jatkuu
            )
        }

        // Simuloidaan pukin vastaus pienellä viiveellä
        viewModelScope.launch {
            delay(1200) // 1.2s
            _uiState.update { state ->
                state.copy(
                    status = "replied",
                    replyText = "Ho ho ho! Kiitos kirjeestäsi 🎅🎁\nTerveisin, Joulupukki",
                    error = null // piilotetaan virhe, jotta UX on siisti
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
