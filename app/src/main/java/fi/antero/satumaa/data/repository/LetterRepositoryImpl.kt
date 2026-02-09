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
 * LetterRepositoryImpl toteuttaa kirjeiden hallinnan "Offline-First" -periaatteella.
 *
 * Logiikka:
 * 1. **Single Source of Truth:** UI lukee dataa VAIN paikallisesta Room-tietokannasta (getLetters).
 * 2. **Synkronointi:** refreshLetters hakee dataa pilvestä ja päivittää Roomia.
 * 3. **Optimistinen päivitys:** sendLetter tallentaa kirjeen heti paikallisesti, jotta UI reagoi viiveettä.
 * 4. **Taustatyöt:** Poistot hoidetaan WorkManagerilla varmuuden vuoksi.
 */
class LetterRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val letterDao: LetterDao,
    private val firestoreSource: LetterFirestoreSource, // Datan hakuun
    private val functionsSource: LetterFunctionsSource, // Lähetykseen (sisältää logiikan)
    @ApplicationContext private val context: Context
) : LetterRepository {

    private val workManager by lazy { WorkManager.getInstance(context) }

    /**
     * Yhdistää kaksi tietokantataulua (letters ja letter_local_state) yhdeksi virraksi.
     * * 'combine' on tehokas operaattori, joka kuuntelee molempia tauluja.
     * Kun kumpikaan muuttuu, se ajaa lambda-funktion ja tuottaa uuden listan.
     */
    override fun getLetters(): Flow<List<Letter>> {
        val userId = auth.currentUser?.uid ?: return emptyFlow()

        val lettersFlow = letterDao.getLettersOnly(userId)
        val statesFlow = letterDao.getAllLocalStates()

        return lettersFlow.combine(statesFlow) { letters, states ->
            // Luodaan hakukartta tiloille nopeaa hakua varten (O(1))
            val stateMap = states.associate { it.letterId to it.isOpened }

            letters.map { letterEntity ->
                // Haetaan tila kartasta, oletuksena false
                val isOpened = stateMap[letterEntity.id] ?: false

                // Manuaalinen mäppäys Domain-malliksi
                Letter(
                    id = letterEntity.id,
                    userId = letterEntity.userId,
                    letterText = letterEntity.letterText,
                    status = letterEntity.status,
                    // Muunnetaan Long (ms) -> Firebase Timestamp
                    createdAt = Timestamp(letterEntity.createdAt / 1000, ((letterEntity.createdAt % 1000) * 1000000).toInt()),
                    replyText = letterEntity.replyText,
                    repliedAt = letterEntity.repliedAt?.let { Timestamp(it / 1000, ((it % 1000) * 1000000).toInt()) },
                    isOpened = isOpened
                )
            }
        }
    }

    /**
     * Hakee yksittäisen kirjeen suoraan tietokannasta ilman Flow'ta.
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
     * Hakee kirjeet pilvestä ja tallentaa ne paikalliseen tietokantaan.
     * Roomin 'OnConflictStrategy.REPLACE' hoitaa päivityksen.
     */
    override suspend fun refreshLetters() {
        val dtos = firestoreSource.getUserLetters()
        if (dtos.isNotEmpty()) {
            val entities = dtos.map { it.toEntity() }
            letterDao.insertLetters(entities)
        }
    }

    /**
     * Lähettää kirjeen.
     * * 1. Tarkistaa verkkoyhteyden.
     * 2. Kutsuu FunctionsSourcea (validointi + tallennus).
     * 3. Onnistuessa tallentaa kirjeen heti paikalliseen kantaan ("Optimistic Update"),
     * jolloin se ilmestyy UI:han välittömästi ilman verkkolatausta.
     */
    override suspend fun sendLetter(letterText: String, childName: String): Result<String> {
        val uid = auth.currentUser?.uid
            ?: return Result.failure(IllegalStateException("AUTH_REQUIRED"))

        if (!isOnline()) {
            return Result.failure(Exception("NETWORK_ERROR"))
        }

        val result = functionsSource.sendLetter(letterText, childName)

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
     * * 1. Poistaa heti paikallisesti (UI päivittyy).
     * 2. Aikatauluttaa WorkManager-työn poistamaan kirjeen pilvestä.
     * Tämä varmistaa poiston, vaikka sovellus suljettaisiin tai verkko katkeaisi.
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

    // Apufunktio verkon tilan tarkistukseen
    private fun isOnline(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}