package fi.antero.satumaa.data.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.work.*
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.qualifiers.ApplicationContext
import fi.antero.satumaa.data.local.dao.LetterDao
import fi.antero.satumaa.data.local.entity.LetterEntity
import fi.antero.satumaa.data.mapper.toEntity
import fi.antero.satumaa.data.model.Letter
import fi.antero.satumaa.data.remote.firestore.LetterFirestoreSource
import fi.antero.satumaa.data.remote.functions.LetterFunctionsSource
import fi.antero.satumaa.workers.DeleteLetterWorker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emptyFlow
import javax.inject.Inject

/**
 * LetterRepositoryImpl hallinnoi kirjeiden dataa.
 *
 * Se noudattaa "Single Source of Truth" -periaatetta:
 * 1. UI lukee aina paikallisesta Room-tietokannasta (getLetters).
 * 2. Uudet tiedot haetaan pilvestä (refreshLetters) ja tallennetaan Roomiin.
 * 3. Lähetys (sendLetter) delegoidaan RemoteSourcelle ja onnistuessa päivitetään Room.
 */
class LetterRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val letterDao: LetterDao,
    private val firestoreSource: LetterFirestoreSource,
    private val functionsSource: LetterFunctionsSource, // UUSI: Hoitaa lähetyksen
    @ApplicationContext private val context: Context
) : LetterRepository {

    private val workManager by lazy { WorkManager.getInstance(context) }

    /**
     * Hakee kirjeet reaktiivisena virtana (Flow).
     * Yhdistää (combine) kaksi tietolähdettä:
     * 1. Itse kirjeet (LetterEntity)
     * 2. Kirjeiden paikallinen tila (LetterStateEntity), esim. onko avattu.
     */
    override fun getLetters(): Flow<List<Letter>> {
        val userId = auth.currentUser?.uid ?: return emptyFlow()

        val lettersFlow = letterDao.getLettersOnly(userId)
        val statesFlow = letterDao.getAllLocalStates()

        return lettersFlow.combine(statesFlow) { letters, states ->
            val stateMap = states.associate { it.letterId to it.isOpened }

            letters.map { letterEntity ->
                val isOpened = stateMap[letterEntity.id] ?: false

                Letter(
                    id = letterEntity.id,
                    userId = letterEntity.userId,
                    letterText = letterEntity.letterText,
                    status = letterEntity.status,
                    // Timestamp-muunnokset
                    createdAt = Timestamp(letterEntity.createdAt / 1000, ((letterEntity.createdAt % 1000) * 1000000).toInt()),
                    replyText = letterEntity.replyText,
                    repliedAt = letterEntity.repliedAt?.let { Timestamp(it / 1000, ((it % 1000) * 1000000).toInt()) },
                    isOpened = isOpened
                )
            }
        }
    }

    /**
     * Hakee yksittäisen kirjeen suoraan Roomista.
     */
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

    /**
     * Hakee uusimmat kirjeet pilvestä ja päivittää paikallisen tietokannan.
     */
    override suspend fun refreshLetters() {
        val dtos = firestoreSource.getUserLetters()
        if (dtos.isNotEmpty()) {
            val entities = dtos.map { it.toEntity() }
            letterDao.insertLetters(entities)
        }
    }

    /**
     * Lähettää uuden kirjeen Joulupukille.
     * Delegoi varsinaisen verkkokutsun LetterFunctionsSourcelle.
     */
    override suspend fun sendLetter(letterText: String, childName: String): Result<String> {
        val uid = auth.currentUser?.uid
            ?: return Result.failure(IllegalStateException("AUTH_REQUIRED"))

        if (!isOnline()) {
            return Result.failure(Exception("NETWORK_ERROR"))
        }

        // Kutsutaan Remote Sourcea (pilvitallennus)
        val result = functionsSource.sendLetter(letterText, childName)

        // Jos onnistui, tallennetaan heti paikallisesti (Optimistic UI update)
        result.onSuccess { docId ->
            val newLetterEntity = LetterEntity(
                id = docId,
                userId = uid,
                childName = childName,
                letterText = letterText,
                replyText = null,
                status = "replying",
                createdAt = System.currentTimeMillis(),
                repliedAt = null
            )
            letterDao.insertLetter(newLetterEntity)
        }

        return result
    }

    /**
     * Poistaa kirjeen.
     * Poistaa ensin paikallisesti ja asettaa taustatyön (Worker) poistamaan pilvestä.
     */
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