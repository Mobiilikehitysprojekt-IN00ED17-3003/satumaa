package fi.antero.satumaa.data.repository

import android.content.Context
import android.util.Log
import androidx.work.*
import dagger.hilt.android.qualifiers.ApplicationContext
import fi.antero.satumaa.data.local.dao.StoryDao
import fi.antero.satumaa.data.mapper.toDomainModel
import fi.antero.satumaa.data.mapper.toEntity
import fi.antero.satumaa.data.model.Story
import fi.antero.satumaa.data.remote.firestore.StoryFirestoreSource
import fi.antero.satumaa.data.remote.functions.StoryFunctionsSource
import fi.antero.satumaa.workers.DeleteStoryWorker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StoryRepository @Inject constructor(
    private val firestoreSource: StoryFirestoreSource, // UUSI
    private val functionsSource: StoryFunctionsSource, // UUSI
    private val storyDao: StoryDao,
    @ApplicationContext private val context: Context
) {

    // 1. Datan haku (UI kuuntelee tätä)
    fun getStories(): Flow<List<Story>> {
        return storyDao.getAllStories().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    suspend fun getStory(id: String): Story? {
        return storyDao.getStoryById(id)?.toDomainModel()
    }

    // 2. Synkronointi (Remote -> Mapper -> Local)
    suspend fun refreshStories() {
        Log.d("StoryRepository", "Synkronoidaan sadut...")

        // Hae DTO:t
        val dtos = firestoreSource.getUserStories()

        if (dtos.isNotEmpty()) {
            // Muunna Entityiksi ja tallenna kantaan
            val entities = dtos.map { it.toEntity() }
            storyDao.insertStories(entities)
            Log.d("StoryRepository", "Tallennettu ${entities.size} satua tietokantaan.")
        } else {
            Log.d("StoryRepository", "Ei satuja pilvessä tai virhe haussa.")
        }
    }

    // 3. Generointi (Functions -> Remote -> Mapper -> Local)
    suspend fun generateAndSaveStory(
        childName: String,
        keywords: List<String>,
        length: String,
        style: String
    ): Result<String> {

        // A) Kutsu Cloud Functionia
        val generateResult = functionsSource.generateStory(childName, keywords, length, style)

        return generateResult.mapCatching { storyId ->
            // B) Jos onnistui, hae valmis satu Firestoresta
            val storyDto = firestoreSource.getStoryById(storyId)
                ?: throw Exception("Satu luotiin, mutta sitä ei löytynyt haettaessa.")

            // C) Tallenna paikalliseen kantaan
            storyDao.insertStory(storyDto.toEntity())

            Log.d("StoryRepository", "Uusi satu tallennettu: $storyId")
            storyId
        }
    }

    // 4. Poisto (Local + WorkManager)
    suspend fun deleteStory(storyId: String) {
        // Poista heti UI:sta
        storyDao.deleteStory(storyId)

        // Jonota pilvipoisto
        val workRequest = OneTimeWorkRequestBuilder<DeleteStoryWorker>()
            .setInputData(workDataOf("STORY_ID" to storyId))
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 10, TimeUnit.SECONDS)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "delete_$storyId",
            ExistingWorkPolicy.KEEP,
            workRequest
        )
    }
}