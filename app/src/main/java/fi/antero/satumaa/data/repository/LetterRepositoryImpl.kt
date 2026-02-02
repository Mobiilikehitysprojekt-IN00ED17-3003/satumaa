package fi.antero.satumaa.data.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.work.*
import dagger.hilt.android.qualifiers.ApplicationContext
import fi.antero.satumaa.data.local.dao.LetterDao
import fi.antero.satumaa.data.local.entity.LetterEntity
import fi.antero.satumaa.data.mapper.toEntity
import fi.antero.satumaa.data.model.Letter
import fi.antero.satumaa.data.remote.firestore.LetterFirestoreSource
import fi.antero.satumaa.workers.DeleteLetterWorker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.tasks.await
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
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
        val userId = auth.currentUser?.uid ?: return emptyFlow()

        val lettersFlow = letterDao.getLettersOnly(userId)
        val statesFlow = letterDao.getAllLocalStates()

        return lettersFlow.combine(statesFlow) { letters, states ->
            val stateMap = states.associate { it.letterId to it.isOpened }

            val result = letters.map { letterEntity ->
                val isOpened = stateMap[letterEntity.id] ?: false

                Letter(
                    id = letterEntity.id,
                    userId = letterEntity.userId,
                    letterText = letterEntity.letterText,
                    status = letterEntity.status,
                    createdAt = Timestamp(letterEntity.createdAt / 1000, ((letterEntity.createdAt % 1000) * 1000000).toInt()),
                    replyText = letterEntity.replyText,
                    repliedAt = letterEntity.repliedAt?.let { Timestamp(it / 1000, ((it % 1000) * 1000000).toInt()) },
                    isOpened = isOpened
                )
            }
            result
        }
    }

    override suspend fun getLetterById(id: String): Letter? {
        val entity = letterDao.getLetterEntityById(id) ?: return null
        val state = letterDao.getLocalStateById(id)
        val isOpened = state?.isOpened ?: false

        return Letter(
            id = entity.id,
            userId = entity.userId,
            letterText = entity.letterText,
            status = entity.status,
            createdAt = Timestamp(entity.createdAt / 1000, ((entity.createdAt % 1000) * 1000000).toInt()),
            replyText = entity.replyText,
            repliedAt = entity.repliedAt?.let { Timestamp(it / 1000, ((it % 1000) * 1000000).toInt()) },
            isOpened = isOpened
        )
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
            ?: return Result.failure(IllegalStateException("AUTH_REQUIRED"))

        if (!isOnline()) {
            return Result.failure(Exception("NETWORK_ERROR"))
        }

        val collectionRef = db.collection("users").document(uid).collection("letters")

        try {
            val countQuery = collectionRef.count().get(AggregateSource.SERVER).await()
            if (countQuery.count >= 10) {
                return Result.failure(Exception("MAILBOX_FULL"))
            }

            val lastLetterQuery = collectionRef
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .await()

            if (!lastLetterQuery.isEmpty) {
                val lastDoc = lastLetterQuery.documents[0]
                val lastDate = lastDoc.getTimestamp("createdAt")?.toDate()
                if (lastDate != null) {
                    val diff = java.util.Date().time - lastDate.time
                    if (diff < 60 * 1000) {
                        return Result.failure(Exception("RATE_LIMIT_LETTER"))
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
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
            val msg = e.message ?: ""
            if (msg.contains("PERMISSION_DENIED", ignoreCase = true) ||
                msg.contains("Missing or insufficient permissions")) {
                Result.failure(Exception("MAILBOX_FULL"))
            } else {
                Result.failure(e)
            }
        }
    }

    override suspend fun deleteLetter(letterId: String) {
        letterDao.deleteLetter(letterId)
        val workRequest = OneTimeWorkRequest.Builder(DeleteLetterWorker::class.java)
            .setInputData(workDataOf("letterId" to letterId))
            .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
            .build()
        workManager.enqueue(workRequest)
    }

    override suspend fun markAsOpened(id: String) {
        letterDao.markAsOpened(id)
    }

    private fun isOnline(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}