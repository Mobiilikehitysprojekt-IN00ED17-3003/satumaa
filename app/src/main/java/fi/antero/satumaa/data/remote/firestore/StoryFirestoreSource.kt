package fi.antero.satumaa.data.remote.firestore

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import fi.antero.satumaa.data.remote.dto.StoryDto
import fi.antero.satumaa.data.remote.dto.toStoryDto
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 * StoryFirestoreSource vastaa satujen hakemisesta Firestoresta.
 * Sadut tallennetaan käyttäjäkohtaiseen alikokoelmaan.
 */
class StoryFirestoreSource @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {

    /**
     * Hakee kaikki käyttäjän tallentamat sadut pilvestä.
     *
     * Polku: users/{userId}/stories
     * Järjestys: Uusin ensin (createdAt DESC)
     */
    suspend fun getUserStories(): List<StoryDto> {
        val userId = auth.currentUser?.uid ?: return emptyList()

        return try {
            val snapshot = firestore.collection("users")
                .document(userId)
                .collection("stories")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()

            snapshot.documents.mapNotNull { it.toStoryDto() }
        } catch (e: Exception) {
            // Hiljainen epäonnistuminen tukee Offline-First -arkkitehtuuria.
            // Jos verkkoyhteyttä ei ole, Repository palauttaa vain paikalliset sadut.
            emptyList()
        }
    }

    /**
     * Hakee yksittäisen sadun tiedot pilvestä.
     * Käytetään esimerkiksi varmistamaan, että satu on olemassa ennen muokkausta.
     *
     * @param storyId Sadun tunniste.
     * @return StoryDto tai null, jos satua ei löydy tai käyttäjä ei ole kirjautunut.
     */
    suspend fun getStoryById(storyId: String): StoryDto? {
        val userId = auth.currentUser?.uid ?: return null

        val snapshot = firestore.collection("users")
            .document(userId)
            .collection("stories")
            .document(storyId)
            .get()
            .await()

        return snapshot.toStoryDto()
    }
}