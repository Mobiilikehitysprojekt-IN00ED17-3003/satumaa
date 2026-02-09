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

/**
 * Taustatyö sadun poistamiseksi pilvestä.
 *
 * Kun käyttäjä poistaa sadun, se poistetaan HETI paikallisesta kannasta (UI päivittyy).
 * Tämä worker varmistaa, että poisto menee perille myös Firestoreen, vaikka
 * verkko pätkisi poistohetkellä.
 */
@HiltWorker
class DeleteStoryWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val firestore: FirebaseFirestore
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        // Haetaan poistettavan sadun ID syötteestä
        val storyId = inputData.getString("STORY_ID") ?: return Result.failure()

        return try {
            Log.d("DeleteStoryWorker", "Deleting story from cloud: $storyId")

            // Huom: Workerissa meidän täytyy hakea auth-instanssi tai injektoida se.
            // Tässä käytetään suoraa getInstance() yksinkertaisuuden vuoksi,
            // mutta Auth-injektio olisi myös validi vaihtoehto.
            val auth = com.google.firebase.auth.FirebaseAuth.getInstance()
            val userId = auth.currentUser?.uid

            if (userId != null) {
                firestore.collection("users")
                    .document(userId)
                    .collection("stories")
                    .document(storyId)
                    .delete()
                    .await()

                Log.d("DeleteStoryWorker", "Story deleted successfully.")
                Result.success()
            } else {
                // Jos käyttäjä on ehtinyt kirjautua ulos, emme voi poistaa pilvestä.
                Log.e("DeleteStoryWorker", "No logged-in user, cannot delete from cloud.")
                Result.failure()
            }

        } catch (e: Exception) {
            Log.e("DeleteStoryWorker", "Error deleting story", e)
            // Yritetään uudelleen (max 3 kertaa)
            if (runAttemptCount < 3) Result.retry() else Result.failure()
        }
    }
}