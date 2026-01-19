package fi.antero.satumaa.data.remote.firestore

import android.util.Log
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
            Log.e("StoryFirestoreSource", "Virhe haettaessa satuja", e)
            emptyList()
        }
    }

    suspend fun getStoryById(storyId: String): StoryDto? {
        val userId = auth.currentUser?.uid ?: return null
        return try {
            val snapshot = firestore.collection("users")
                .document(userId)
                .collection("stories")
                .document(storyId)
                .get()
                .await()

            snapshot.toStoryDto()
        } catch (e: Exception) {
            Log.e("StoryFirestoreSource", "Virhe haettaessa yksittäistä satua", e)
            null
        }
    }
}