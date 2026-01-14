package fi.antero.satumaa.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions
import fi.antero.satumaa.data.local.dao.StoryDao
import fi.antero.satumaa.data.local.entity.StoryEntity
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StoryRepository @Inject constructor(
    private val functions: FirebaseFunctions,
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val storyDao: StoryDao
) {

    suspend fun generateAndSaveStory(
        childName: String,
        keywords: List<String>,
        length: String,
        style: String
    ): Result<String> {

        val user = auth.currentUser
            ?: return Result.failure(Exception("Et ole kirjautunut sisään. Kirjaudu ensin."))

        return try {
            user.getIdToken(true).await()

            val data = hashMapOf(
                "childName" to childName,
                "keywords" to keywords,
                "length" to length,
                "style" to style
            )

            Log.d("StoryRepository", "Kutsutaan Cloud Functionia: generateStory, uid=${user.uid}")

            val result = functions
                .getHttpsCallable("generateStory")
                .call(data)
                .await()

            val response = result.data as? Map<*, *>
                ?: throw Exception("Virheellinen vastaus pilvestä (ei Map)")

            val storyId = response["storyId"] as? String
                ?: throw Exception("Ei saatu storyId:tä pilvestä")

            val uidUsed = (response["uidUsed"] as? String) ?: user.uid

            Log.d("StoryRepository", "Satu luotu. storyId=$storyId uidUsed=$uidUsed. Haetaan sisältö Firestoresta...")

            val snapshot = firestore.collection("users")
                .document(uidUsed)
                .collection("stories")
                .document(storyId)
                .get()
                .await()

            if (!snapshot.exists()) {
                throw Exception("Satu ei löytynyt Firestoresta (storyId=$storyId, uid=$uidUsed)")
            }

            val title = snapshot.getString("title") ?: "Nimetön satu"
            val content = snapshot.getString("content") ?: "Ei sisältöä."
            val keywordsString = keywords.joinToString(", ")

            val entity = StoryEntity(
                id = storyId,
                title = title,
                content = content,
                childName = childName,
                style = style,
                keywords = keywordsString,
                createdAt = System.currentTimeMillis(),
                isFavorite = false
            )

            storyDao.insertStory(entity)

            Log.d("StoryRepository", "Satu tallennettu paikallisesti: $storyId")

            Result.success(storyId)
        } catch (e: Exception) {
            Log.e("StoryRepository", "Virhe sadun luonnissa", e)

            val crashlytics = FirebaseCrashlytics.getInstance()
            crashlytics.setCustomKey("story_style", style)
            crashlytics.setCustomKey("story_length", length)
            crashlytics.setCustomKey("user_uid", auth.currentUser?.uid ?: "null")
            crashlytics.log("Epäonnistui sadun luonnissa (generateStory)")
            crashlytics.recordException(e)

            Result.failure(e)
        }
    }

    fun getStories() = storyDao.getAllStories()
    suspend fun getStory(id: String) = storyDao.getStoryById(id)
}
