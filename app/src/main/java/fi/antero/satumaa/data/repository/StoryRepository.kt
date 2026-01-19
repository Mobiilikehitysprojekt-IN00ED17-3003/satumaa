package fi.antero.satumaa.data.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
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
    private val firestoreSource: StoryFirestoreSource,
    private val functionsSource: StoryFunctionsSource,
    private val storyDao: StoryDao,
    @ApplicationContext private val context: Context
) {

    fun getStories(): Flow<List<Story>> {
        return storyDao.getAllStories().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    suspend fun getStory(id: String): Story? {
        return storyDao.getStoryById(id)?.toDomainModel()
    }

    suspend fun refreshStories() {
        Log.d("StoryRepository", "Synkronoidaan sadut...")
        val dtos = firestoreSource.getUserStories()
        if (dtos.isNotEmpty()) {
            val entities = dtos.map { it.toEntity() }
            storyDao.insertStories(entities)
            Log.d("StoryRepository", "Tallennettu ${entities.size} satua tietokantaan.")
        }
    }

    // --- KORJATTU METODI VERKON TARKISTUKSELLA ---
    suspend fun generateAndSaveStory(
        childName: String,
        keywords: List<String>,
        length: String,
        style: String
    ): Result<String> {

        // 1. Tarkistetaan onko laite verkossa ennen kuin yritetään mitään
        if (!isOnline()) {
            return Result.failure(Exception("Ei verkkoyhteyttä. Tarkista netti ja yritä uudelleen."))
        }

        // 2. Kutsu Cloud Functionia
        val generateResult = functionsSource.generateStory(childName, keywords, length, style)

        return generateResult.mapCatching { storyId ->
            val storyDto = firestoreSource.getStoryById(storyId)
                ?: throw Exception("Satu luotiin, mutta sitä ei löytynyt haettaessa.")

            storyDao.insertStory(storyDto.toEntity())
            Log.d("StoryRepository", "Uusi satu tallennettu: $storyId")
            storyId
        }
    }

    // Apufunktio verkon tilan tarkistamiseen
    private fun isOnline(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    suspend fun deleteStory(storyId: String) {
        storyDao.deleteStory(storyId)

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