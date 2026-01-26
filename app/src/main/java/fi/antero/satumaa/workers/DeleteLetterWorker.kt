package fi.antero.satumaa.workers

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import fi.antero.satumaa.data.remote.firestore.LetterFirestoreSource

@HiltWorker
class DeleteLetterWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val firestoreSource: LetterFirestoreSource
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val letterId = inputData.getString("letterId") ?: return Result.failure()

        return try {
            Log.d("DeleteWorker", "Poistetaan kirjettä pilvestä: $letterId")
            firestoreSource.deleteLetter(letterId)
            Result.success()
        } catch (e: Exception) {
            Log.e("DeleteWorker", "Poisto epäonnistui, yritetään myöhemmin", e)
            if (runAttemptCount < 3) {
                Result.retry()
            } else {
                Result.failure()
            }
        }
    }
}