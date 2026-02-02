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
class StoryRepositoryImpl @Inject constructor(
    private val firestoreSource: StoryFirestoreSource,
    private val functionsSource: StoryFunctionsSource,
    private val storyDao: StoryDao,
    @ApplicationContext private val context: Context
) : StoryRepository {

    private val workManager by lazy { WorkManager.getInstance(context) }

    override fun getStories(): Flow<List<Story>> {
        return storyDao.getAllStories().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override suspend fun getStory(id: String): Story? {
        return storyDao.getStoryById(id)?.toDomainModel()
    }

    override suspend fun refreshStories() {
        try {
            val dtos = firestoreSource.getUserStories()
            if (dtos.isNotEmpty()) {
                val entities = dtos.map { it.toEntity() }
                storyDao.insertStories(entities)
            }
        } catch (e: Exception) {
            Log.e("StoryRepository", "Synkronointi epäonnistui: ${e.message}")
        }
    }

    override suspend fun generateStoryPreview(
        childName: String,
        keywords: List<String>,
        length: String,
        style: String
    ): Result<Story> {
        if (!isOnline()) {
            return Result.failure(Exception("NETWORK_ERROR"))
        }
        return functionsSource.generateStory(childName, keywords, length, style)
    }

    override suspend fun saveStory(story: Story): Result<String> {
        if (!isOnline()) {
            return Result.failure(Exception("NETWORK_ERROR"))
        }

        val cloudResult = functionsSource.saveStoryToCloud(story)

        return cloudResult.mapCatching { newId ->
            val savedStory = story.copy(id = newId)
            storyDao.insertStory(savedStory.toEntity())
            newId
        }
    }

    override suspend fun deleteStory(storyId: String) {
        storyDao.deleteStory(storyId)

        val workRequest = OneTimeWorkRequestBuilder<DeleteStoryWorker>()
            .setInputData(workDataOf("STORY_ID" to storyId))
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 10, TimeUnit.SECONDS)
            .build()

        workManager.enqueueUniqueWork(
            "delete_$storyId",
            ExistingWorkPolicy.KEEP,
            workRequest
        )
    }

    private fun isOnline(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}