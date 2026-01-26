package fi.antero.satumaa.data.remote.firestore

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import fi.antero.satumaa.data.remote.dto.LetterDto
import fi.antero.satumaa.data.remote.dto.toLetterDto
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class LetterFirestoreSource @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {

    // Hakee käyttäjän kaikki kirjeet
    suspend fun getUserLetters(): List<LetterDto> {
        val userId = auth.currentUser?.uid ?: return emptyList()

        return try {
            val snapshot = firestore.collection("users")
                .document(userId)
                .collection("letters")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()

            snapshot.documents.mapNotNull { it.toLetterDto() }
        } catch (e: Exception) {
            Log.e("LetterFirestoreSource", "Virhe haettaessa kirjeitä", e)
            emptyList()
        }
    }

    suspend fun deleteLetter(letterId: String) {
        val userId = auth.currentUser?.uid ?: return
        try {
            firestore.collection("users")
                .document(userId)
                .collection("letters")
                .document(letterId)
                .delete()
                .await()
        } catch (e: Exception) {
            Log.e("LetterFirestoreSource", "Virhe poistettaessa kirjettä: $letterId", e)
            throw e // Heitetään virhe, jotta Worker tietää yrittää uudelleen
        }
    }
}