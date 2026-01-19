package fi.antero.satumaa.workers

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.firestore.FirebaseFirestore
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.tasks.await

@HiltWorker
class DeleteStoryWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val firestore: FirebaseFirestore
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val storyId = inputData.getString("STORY_ID") ?: return Result.failure()

        return try {
            Log.d("DeleteWorker", "Poistetaan satua pilvestä: $storyId")


            val auth = com.google.firebase.auth.FirebaseAuth.getInstance()
            val userId = auth.currentUser?.uid

            if (userId != null) {
                firestore.collection("users")
                    .document(userId)
                    .collection("stories")
                    .document(storyId)
                    .delete()
                    .await()
                Log.d("DeleteWorker", "Satu poistettu onnistuneesti.")
                Result.success()
            } else {
                Log.e("DeleteWorker", "Ei kirjautunutta käyttäjää, ei voida poistaa pilvestä.")
                Result.failure()
            }

        } catch (e: Exception) {
            Log.e("DeleteWorker", "Virhe poistossa", e)
            if (runAttemptCount < 3) Result.retry() else Result.failure()
        }
    }
}