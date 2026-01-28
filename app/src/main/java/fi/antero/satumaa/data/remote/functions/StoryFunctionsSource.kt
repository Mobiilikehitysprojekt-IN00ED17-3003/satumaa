package fi.antero.satumaa.data.remote.functions

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.functions.FirebaseFunctions
import fi.antero.satumaa.data.model.Story
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class StoryFunctionsSource @Inject constructor(
    private val functions: FirebaseFunctions,
    private val auth: FirebaseAuth
) {
    suspend fun generateStory(
        childName: String,
        keywords: List<String>,
        length: String,
        style: String
    ): Result<Story> {
        val user = auth.currentUser
            ?: return Result.failure(Exception("AUTH_REQUIRED"))

        return try {
            // Varmistetaan tokenin tuoreus
            user.getIdToken(true).await()

            val data = hashMapOf(
                "childName" to childName,
                "keywords" to keywords,
                "length" to length,
                "style" to style
            )

            val result = functions
                .getHttpsCallable("generateStory")
                .withTimeout(60, TimeUnit.SECONDS)
                .call(data)
                .await()

            val response = result.data as? Map<*, *>
            val storyMap = response?.get("story") as? Map<*, *>
            val previewId = response?.get("previewId") as? String

            if (storyMap != null) {
                val story = Story(
                    id = "",
                    title = storyMap["title"] as? String ?: "Nimetön satu",
                    content = storyMap["content"] as? String ?: "",
                    childName = storyMap["childName"] as? String ?: childName,
                    style = storyMap["style"] as? String ?: style,
                    keywords = (storyMap["keywords"] as? List<*>)?.joinToString(", ") ?: "",
                    createdAt = System.currentTimeMillis(),
                    isFavorite = false,
                    previewId = previewId
                )
                Result.success(story)
            } else {
                Result.failure(Exception("EMPTY_RESPONSE"))
            }
        } catch (e: Exception) {
            // Virhe (esim. RATE_LIMIT_STORY) nousee suoraan ylös kääntäjälle
            Result.failure(e)
        }
    }

    suspend fun saveStoryToCloud(story: Story): Result<String> {
        val user = auth.currentUser
            ?: return Result.failure(Exception("AUTH_REQUIRED"))

        return try {
            val keywordsList = story.keywords.split(",").map { it.trim() }.filter { it.isNotEmpty() }

            val data = hashMapOf(
                "previewId" to (story.previewId ?: ""),
                "title" to story.title,
                "content" to story.content,
                "childName" to story.childName,
                "style" to story.style,
                "keywords" to keywordsList
            )

            val result = functions
                .getHttpsCallable("saveStory")
                .call(data)
                .await()

            val response = result.data as? Map<*, *>
            val newId = response?.get("storyId") as? String

            if (newId != null) {
                Result.success(newId)
            } else {
                Result.failure(Exception("SAVE_ID_MISSING"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}