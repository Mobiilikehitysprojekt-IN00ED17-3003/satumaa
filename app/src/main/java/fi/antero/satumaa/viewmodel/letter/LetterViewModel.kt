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


class LetterViewModel(
    private val repo: LetterRepository = LetterRepositoryImpl(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) : ViewModel() {


    private val _uiState = MutableStateFlow(LetterUiState())
    val uiState: StateFlow<LetterUiState> = _uiState

    // Firestore-kuuntelija
    private var listener: ListenerRegistration? = null


    fun onTextChange(text: String) {
        _uiState.update { it.copy(text = text, error = null) }
    }


    // kenttään on pakkko kirjoittaa jotain ennen lähetystä, muuten - error
    fun sendLetter() {
        val content = uiState.value.text.trim()
        if (content.isEmpty()) {
            _uiState.update { it.copy(error = "Kirjoita kirje ennen lähettämistä") }
            return
        }

        // lähetys -> UI: "lähetetään" ja status "replying"
        _uiState.update {
            it.copy(
                isSending = true,
                status = "replying",
                error = null,
                replyText = null,
                showReplyArrived = false // nollataan ilmoitus joka lähetystä kohti
            )
        }

        // Firestore yhdistys
        repo.sendLetter(content)
            .addOnSuccessListener { doc ->
                // Kun kirje on tallennettu, tyhjennetään tekstikenttä ja lopetetaan spinner
                _uiState.update {
                    it.copy(
                        text = "",
                        isSending = false,
                        usedOfflineDemo = false
                    )
                }
                // vastauksen kuuntelu
                listenToLetter(doc.id)
            }
            .addOnFailureListener { e ->
                // Backend ei käytössä / oikeudet puuttuvat -> offline demo
                startOfflineDemoReply(
                    originalError = e.message ?: "Backend ei käytössä / oikeudet puuttuvat"
                )
            }
    }


    fun simulateReply() {
        startOfflineDemoReply(originalError = null)
    }


    // offline demossa kun on kehitetty tms kätetään tätä: (voi poistaa kun valmis, tai backi toimii)
    private fun startOfflineDemoReply(originalError: String?) {
        // Lopetetaan firestoren kuuntelu
        listener?.remove()
        listener = null

        // UI "replying" tilaan ja näytetään mahdollinen virhe info-muodossa
        _uiState.update {
            it.copy(
                isSending = false,
                status = "replying",
                usedOfflineDemo = true,
                error = originalError,
                showReplyArrived = false
            )
        }

        // demo AI:n miettiminen
        viewModelScope.launch {
            delay(1200)

            // tilan päivittäminen kun tulee vastaujs
            _uiState.update { state ->
                state.copy(
                    status = "replied",
                    replyText = "Ho ho ho! Kiitos kirjeestäsi 🎅🎁\nTerveisin, Joulupukki",
                    error = null,
                    showReplyArrived = true
                )
            }
        }
    }

    // firestoresta id tarkistus ja kun status vaihtuu replied tilaan -> showreply= true vain kerran
    private fun listenToLetter(id: String) {
        listener?.remove()

        listener = db.collection("letters")
            .document(id)
            .addSnapshotListener { snap, err ->
                if (err != null || snap == null) return@addSnapshotListener
                if (!snap.exists()) return@addSnapshotListener

                _uiState.update { prev ->
                    val newStatus = snap.getString("status")
                    val newReply = snap.getString("replyText")

                    // Ilmoitus jos status vaihtuu -> replied
                    val shouldNotify = (prev.status != "replied" && newStatus == "replied")

                    prev.copy(
                        status = newStatus,
                        replyText = newReply,
                        error = null,
                        showReplyArrived = shouldNotify
                    )
                }
            }
    }

    // ui kutsuu vain kerran ni ei näy ilmo uudellee
    fun consumeReplyArrived() {
        _uiState.update { it.copy(showReplyArrived = false) }
    }

    override fun onCleared() {
        listener?.remove()
        super.onCleared()
    }
}
