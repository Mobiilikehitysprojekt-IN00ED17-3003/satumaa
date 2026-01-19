package fi.antero.satumaa.workers

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import fi.antero.satumaa.data.repository.StoryRepository

@HiltWorker
class SyncStoriesWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: StoryRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            Log.d("SyncWorker", "Aloitetaan taustasynkkaus...")
            repository.refreshStories()
            Log.d("SyncWorker", "Taustasynkkaus valmis.")
            Result.success()
        } catch (e: Exception) {
            Log.e("SyncWorker", "Synkkaus epäonnistui", e)
            if (runAttemptCount < 3) {
                Result.retry()
            } else {
                Result.failure()
            }
        }
    }
}