package fi.antero.satumaa.viewmodel.letter

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import fi.antero.satumaa.data.repository.LetterRepository
import fi.antero.satumaa.data.repository.LetterRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update



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

        _uiState.update { it.copy(isSending = true, status = "replying") }

        repo.sendLetter(content)
            .addOnSuccessListener { doc ->
                _uiState.update { it.copy(text = "", isSending = false) }
                listenToLetter(doc.id)
            }
            .addOnFailureListener { e ->
                _uiState.update {
                    it.copy(isSending = false, status = "error", error = e.message)
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
                        replyText = snap.getString("replyText")
                    )
                }
            }
    }

    override fun onCleared() {
        listener?.remove()
        super.onCleared()
    }
}
