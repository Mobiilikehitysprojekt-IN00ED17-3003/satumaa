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

        // Huom: Vaikka backend päästää nyt läpi ilmankin,
        // pidetään tämä tarkistus sovelluksessa hyvän tavan vuoksi.
        if (auth.currentUser == null) {
            return Result.failure(Exception("Et ole kirjautunut sisään. Kirjaudu ensin."))
        }

        return try {
            val data = hashMapOf(
                "childName" to childName,
                "keywords" to keywords,
                "length" to length,
                "style" to style
            )

            Log.d("StoryRepository", "Kutsutaan Cloud Functionia nimellä 'generateStory'...")

            // PALAUTETTU NIMEEN PERUSTUVA KUTSU
            // Nyt kun index.ts export on varmasti 'generateStory' (Iso S), tämä löytää sen.
            // Firebase Functions SDK osaa liittää auth-headerit paremmin näin kuin URL-kutsussa.
            val result = functions
                .getHttpsCallable("generateStory")
                .call(data)
                .await()

            val response = result.data as? Map<String, Any>
            val storyId = response?.get("storyId") as? String
                ?: throw Exception("Ei saatu storyId:tä pilvestä")

            Log.d("StoryRepository", "Satu luotu, ID: $storyId. Haetaan sisältö...")

            // Jos backend käytti testi-käyttäjää (auth puuttui),
            // tämä haku saattaa epäonnistua koska tallennus meni eri paikkaan.
            // Mutta ainakin pääsemme tänne asti!
            val uid = auth.currentUser?.uid ?: "TEST_USER_NO_AUTH"

            val snapshot = firestore.collection("users").document(uid)
                .collection("stories").document(storyId)
                .get().await()

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
            Log.d("StoryRepository", "Satu tallennettu paikallisesti!")

            Result.success(storyId)
        } catch (e: Exception) {
            Log.e("StoryRepository", "Virhe sadun luonnissa", e)
            val crashlytics = FirebaseCrashlytics.getInstance()
            crashlytics.setCustomKey("story_style", style)
            crashlytics.setCustomKey("story_length", length)
            crashlytics.log("Epäonnistui sadun luonnissa")
            crashlytics.recordException(e)
            Result.failure(e)
        }
    }

    fun getStories() = storyDao.getAllStories()
    suspend fun getStory(id: String) = storyDao.getStoryById(id)
}