package fi.antero.satumaa.data.remote.functions

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.functions.FirebaseFunctions
import fi.antero.satumaa.data.model.Story
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * StoryFunctionsSource on rajapinta Firebase Cloud Functions -palveluihin (Backend).
 *
 * Tämä luokka ulkoistaa raskaan prosessoinnin (tekoälygeneroinnin) palvelimelle.
 * Kommunikaatio tapahtuu HTTPS Callable -funktioiden kautta (RPC - Remote Procedure Call).
 */
class StoryFunctionsSource @Inject constructor(
    private val functions: FirebaseFunctions,
    private val auth: FirebaseAuth
) {

    /**
     * Kutsuu 'generateStory'-pilvifunktiota sadun luomiseksi.
     *
     * @param childName Lapsen nimi.
     * @param keywords Lista avainsanoja.
     * @param length Pituusvalinta (esim. "NORMAL").
     * @param style Tyylivalinta (esim. "EXCITING").
     * @return Result<Story>: Onnistuessa generoitu Story-objekti (sisältää previewId:n).
     */
    suspend fun generateStory(
        childName: String,
        keywords: List<String>,
        length: String,
        style: String
    ): Result<Story> {
        val user = auth.currentUser
            ?: return Result.failure(Exception("AUTH_REQUIRED"))

        return try {
            // Pakotetaan tokenin päivitys ennen kutsua, jotta backend saa varmasti validin auth-tiedon
            user.getIdToken(true).await()

            val data = hashMapOf(
                "childName" to childName,
                "keywords" to keywords,
                "length" to length,
                "style" to style
            )

            // Kutsutaan funktiota.
            val result = functions
                .getHttpsCallable("generateStory")
                .withTimeout(60, TimeUnit.SECONDS)
                .call(data)
                .await()

            // Parsitaan vastaus (Map -> Story)
            val response = result.data as? Map<*, *>
            val storyMap = response?.get("story") as? Map<*, *>
            val previewId = response?.get("previewId") as? String

            if (storyMap != null) {
                // Luodaan Domain-malli. ID on tässä vaiheessa tyhjä, koska satua ei ole vielä tallennettu.
                val story = Story(
                    id = "",
                    title = storyMap["title"] as? String ?: "Nimetön satu",
                    content = storyMap["content"] as? String ?: "",
                    childName = storyMap["childName"] as? String ?: childName,
                    style = storyMap["style"] as? String ?: style,
                    keywords = (storyMap["keywords"] as? List<*>)?.joinToString(", ") ?: "",
                    createdAt = System.currentTimeMillis(),
                    isFavorite = false,
                    previewId = previewId // Tärkeä: yhdistää esikatselun tallennukseen
                )
                Result.success(story)
            } else {
                Result.failure(Exception("EMPTY_RESPONSE"))
            }
        } catch (e: Exception) {
            // Backendin palauttamat virheet (esim. RATE_LIMIT_STORY) nousevat täältä
            // ja ne käännetään ErrorUtilsissa suomeksi.
            Result.failure(e)
        }
    }

    /**
     * Kutsuu 'saveStory'-pilvifunktiota generoidun sadun tallentamiseksi.
     *
     * @param story Tallennettava satu (sisältää previewId:n).
     * @return Result<String>: Uuden sadun pysyvä ID (UUID) Firestoresta.
     */
    suspend fun saveStoryToCloud(story: Story): Result<String> {
        val user = auth.currentUser
            ?: return Result.failure(Exception("AUTH_REQUIRED"))

        return try {
            // Muunnetaan avainsanat takaisin listaksi backendiä varten
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