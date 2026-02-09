package fi.antero.satumaa.data.remote.firestore

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import fi.antero.satumaa.data.remote.dto.LetterDto
import fi.antero.satumaa.data.remote.dto.toLetterDto
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 * LetterFirestoreSource hoitaa kaiken liikenteen Firestore-tietokantaan kirjeiden osalta.
 *
 * Tämä luokka ei tiedä mitään paikallisesta tietokannasta tai Roomista.
 * Se vain hakee ja poistaa dataa pilvestä.
 */
class LetterFirestoreSource @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {

    /**
     * Hakee kirjautuneen käyttäjän kaikki kirjeet pilvestä.
     *
     * Polku: users/{userId}/letters
     * Järjestys: Uusin ensin (createdAt DESC)
     *
     * @return Lista LetterDto-objekteja. Jos käyttäjä ei ole kirjautunut tai haku epäonnistuu, palauttaa tyhjän listan.
     */
    suspend fun getUserLetters(): List<LetterDto> {
        val userId = auth.currentUser?.uid ?: return emptyList()

        return try {
            val snapshot = firestore.collection("users")
                .document(userId)
                .collection("letters")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await() // Odotetaan vastausta coroutinessa

            // Muunnetaan Firestoren dokumentit DTO:iksi ja filtteröidään nullit pois
            snapshot.documents.mapNotNull { it.toLetterDto() }
        } catch (e: Exception) {
            // Virhetilanteessa (esim. ei verkkoa) palautetaan tyhjä lista.
            // Repository luottaa tällöin paikalliseen välimuistiin (Offline-First).
            emptyList()
        }
    }

    /**
     * Poistaa yksittäisen kirjeen pilvestä.
     *
     * @param letterId Poistettavan kirjeen ID.
     * @throws Exception Jos poisto epäonnistuu (esim. verkkovirhe), heittää poikkeuksen,
     * jotta WorkManager voi yrittää myöhemmin uudelleen.
     */
    suspend fun deleteLetter(letterId: String) {
        val userId = auth.currentUser?.uid ?: return

        firestore.collection("users")
            .document(userId)
            .collection("letters")
            .document(letterId)
            .delete()
            .await()
    }
}