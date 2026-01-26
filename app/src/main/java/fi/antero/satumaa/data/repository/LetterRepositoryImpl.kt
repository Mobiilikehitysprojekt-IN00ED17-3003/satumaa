package fi.antero.satumaa.data.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.qualifiers.ApplicationContext
import fi.antero.satumaa.data.local.dao.LetterDao
import fi.antero.satumaa.data.local.entity.LetterEntity
import fi.antero.satumaa.data.mapper.toDomainModel
import fi.antero.satumaa.data.mapper.toEntity
import fi.antero.satumaa.data.model.Letter
import fi.antero.satumaa.data.remote.firestore.LetterFirestoreSource
import fi.antero.satumaa.workers.DeleteLetterWorker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class LetterRepositoryImpl @Inject constructor(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val letterDao: LetterDao,
    private val firestoreSource: LetterFirestoreSource,
    @ApplicationContext private val context: Context
) : LetterRepository {

    private val workManager by lazy { WorkManager.getInstance(context) }

    override fun getLetters(): Flow<List<Letter>> {
        val userId = auth.currentUser?.uid ?: return kotlinx.coroutines.flow.emptyFlow()
        return letterDao.getLettersByUserId(userId).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override suspend fun getLetterById(id: String): Letter? {
        return letterDao.getLetterById(id)?.toDomainModel()
    }

    override suspend fun refreshLetters() {
        val dtos = firestoreSource.getUserLetters()
        if (dtos.isNotEmpty()) {
            val entities = dtos.map { it.toEntity() }
            letterDao.insertLetters(entities)
        }
    }

    override suspend fun sendLetter(letterText: String, childName: String): Result<String> {
        val uid = auth.currentUser?.uid
            ?: return Result.failure(IllegalStateException("Käyttäjä ei ole kirjautunut"))

        if (!isOnline()) {
            return Result.failure(Exception("Ei verkkoyhteyttä. Tarkista netti."))
        }

        val data = hashMapOf(
            "userId" to uid,
            "childName" to childName,
            "letterText" to letterText,
            "status" to "replying",
            "createdAt" to FieldValue.serverTimestamp()
        )

        return try {
            val docRef = db.collection("users")
                .document(uid)
                .collection("letters")
                .add(data)
                .await()

            val newLetterEntity = LetterEntity(
                id = docRef.id,
                userId = uid,
                childName = childName,
                letterText = letterText,
                replyText = null,
                status = "replying",
                createdAt = System.currentTimeMillis(),
                repliedAt = null
            )
            letterDao.insertLetter(newLetterEntity)

            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteLetter(letterId: String) {
        letterDao.deleteLetter(letterId)

        val workRequest = OneTimeWorkRequest.Builder(DeleteLetterWorker::class.java)
            .setInputData(workDataOf("letterId" to letterId))
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()

        workManager.enqueue(workRequest)
    }

    private fun isOnline(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}