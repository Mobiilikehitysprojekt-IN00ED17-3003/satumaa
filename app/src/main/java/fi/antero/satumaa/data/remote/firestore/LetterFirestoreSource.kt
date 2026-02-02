package fi.antero.satumaa.data.remote.firestore

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
            // Jos haku epäonnistuu (esim. offline), palautetaan tyhjä lista.
            // Repository pitää huolen, että paikallinen data näytetään silti.
            emptyList()
        }
    }

    // Poistaa kirjeen (WorkManager tai Repository kutsuu)
    suspend fun deleteLetter(letterId: String) {
        val userId = auth.currentUser?.uid ?: return

        // Annetaan mahdollisen virheen (esim. verkkovirhe) nousta ylös,
        // jotta WorkManager tietää yrittää uudelleen.
        firestore.collection("users")
            .document(userId)
            .collection("letters")
            .document(letterId)
            .delete()
            .await()
    }
}