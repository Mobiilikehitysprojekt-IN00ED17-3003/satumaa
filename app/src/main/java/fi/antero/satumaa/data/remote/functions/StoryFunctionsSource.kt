package fi.antero.satumaa.data.remote.functions

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.functions.FirebaseFunctions
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
    ): Result<String> {
        val user = auth.currentUser
            ?: return Result.failure(Exception("Ei kirjautumista"))

        return try {
            user.getIdToken(true).await()

            val data = hashMapOf(
                "childName" to childName,
                "keywords" to keywords,
                "length" to length,
                "style" to style
            )

            // KORJATTU TAPA: Asetetaan timeout uudemmalla syntaksilla
            val result = functions
                .getHttpsCallable("generateStory")
                .withTimeout(30, TimeUnit.SECONDS) // Tämä korvaa vialliset rivit
                .call(data)
                .await()

            val response = result.data as? Map<*, *>
            val storyId = response?.get("storyId") as? String

            if (storyId != null) {
                Result.success(storyId)
            } else {
                Result.failure(Exception("Cloud Function ei palauttanut ID:tä"))
            }
        } catch (e: Exception) {
            Log.e("StoryFunctionsSource", "Cloud Function virhe", e)
            Result.failure(e)
        }
    }
}