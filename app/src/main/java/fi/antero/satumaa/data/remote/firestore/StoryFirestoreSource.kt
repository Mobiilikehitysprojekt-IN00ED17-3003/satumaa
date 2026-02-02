package fi.antero.satumaa.data.remote.firestore

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import fi.antero.satumaa.data.remote.dto.StoryDto
import fi.antero.satumaa.data.remote.dto.toStoryDto
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class StoryFirestoreSource @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {

    /**
     * Hakee kaikki käyttäjän sadut pilvestä.
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
            // Hiljainen epäonnistuminen listan haussa on usein parempi taustasynkkaukselle.
            // Repository käyttää tällöin vain paikallista tietokantaa.
            emptyList()
        }
    }

    /**
     * Hakee yksittäisen sadun pilvestä ID:n perusteella.
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