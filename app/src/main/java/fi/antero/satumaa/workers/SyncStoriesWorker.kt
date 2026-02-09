package fi.antero.satumaa.workers

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import fi.antero.satumaa.data.repository.StoryRepository

/**
 * Taustatyö satujen synkronointiin pilvestä laitteelle.
 *
 * Tätä voidaan kutsua esimerkiksi:
 * 1. Sovelluksen käynnistyessä.
 * 2. Periodisesti (esim. kerran päivässä).
 * 3. Kun käyttäjä painaa "Päivitä"-nappia.
 *
 * @HiltWorker: Mahdollistaa riippuvuuksien (StoryRepository) injektoinnin Workeriin.
 */
@HiltWorker
class SyncStoriesWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: StoryRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            Log.d("SyncStoriesWorker", "Starting background sync...")

            // Kutsutaan repositorya, joka hakee datan Firestoresta ja tallentaa Roomiin.
            repository.refreshStories()

            Log.d("SyncStoriesWorker", "Background sync completed.")
            Result.success()
        } catch (e: Exception) {
            Log.e("SyncStoriesWorker", "Sync failed", e)

            // Jos synkkaus epäonnistuu (esim. verkkovirhe), yritetään uudelleen.
            // WorkManager hoitaa "Exponential Backoff" -viiveen yritysten välillä.
            if (runAttemptCount < 3) {
                Result.retry()
            } else {
                Result.failure()
            }
        }
    }
}