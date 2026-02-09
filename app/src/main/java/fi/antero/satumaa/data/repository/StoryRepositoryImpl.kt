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

/**
 * StoryRepositoryImpl toteuttaa tarinoiden hallinnan.
 *
 * Arkkitehtuuri:
 * 1. Lukeminen: Aina paikallisesta kannasta (Room) -> Nopea, toimii offline.
 * 2. Kirjoitus/Generointi: Vaatii verkon (Cloud Functions).
 * 3. Poisto: Optimistinen (heti pois lokaalista) + Taustatyö (WorkManager pilveen).
 */
@Singleton
class StoryRepositoryImpl @Inject constructor(
    private val firestoreSource: StoryFirestoreSource, // Datan haku pilvestä
    private val functionsSource: StoryFunctionsSource, // AI-generointi ja tallennus
    private val storyDao: StoryDao,                    // Paikallinen välimuisti
    @ApplicationContext private val context: Context
) : StoryRepository {

    private val workManager by lazy { WorkManager.getInstance(context) }

    /**
     * Palauttaa Flow'n, joka muuntaa tietokannan Entityt suoraan Domain-malleiksi.
     * UI päivittyy automaattisesti aina kun DAO muuttuu.
     */
    override fun getStories(): Flow<List<Story>> {
        return storyDao.getAllStories().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override suspend fun getStory(id: String): Story? {
        return storyDao.getStoryById(id)?.toDomainModel()
    }

    /**
     * Hakee kaikki sadut Firestoresta ja tallentaa ne Roomiin.
     * Tämä on "yksisuuntainen synkkaus" (Cloud -> Device).
     */
    override suspend fun refreshStories() {
        try {
            val dtos = firestoreSource.getUserStories()
            if (dtos.isNotEmpty()) {
                val entities = dtos.map { it.toEntity() }
                storyDao.insertStories(entities)
            }
        } catch (e: Exception) {
            // Logitetaan virhe, mutta ei kaadeta sovellusta.
            // Käyttäjä näkee yhä vanhat, välimuistissa olevat sadut.
            Log.e("StoryRepository", "Synkronointi epäonnistui: ${e.message}")
        }
    }

    /**
     * Kutsuu tekoälyä luomaan sadun.
     * Vaatii verkkoyhteyden.
     */
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

    /**
     * Tallentaa generoidun sadun.
     *
     * Logiikka:
     * 1. Yritetään tallentaa pilveen (Cloud Function).
     * 2. Jos onnistuu, saadaan uusi ID.
     * 3. Tallennetaan satu tällä uudella ID:llä paikalliseen kantaan.
     */
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

    /**
     * Poistaa sadun "Optimistic UI" -tavalla.
     * 1. Poistetaan heti paikallisesta kannasta -> katoaa UI:sta heti.
     * 2. Ajastetaan WorkManager-työ poistamaan se pilvestä taustalla.
     */
    override suspend fun deleteStory(storyId: String) {
        storyDao.deleteStory(storyId)

        val workRequest = OneTimeWorkRequestBuilder<DeleteStoryWorker>()
            .setInputData(workDataOf("STORY_ID" to storyId))
            // Yritetään suorittaa heti, jos mahdollista
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            // Vaatii verkkoyhteyden
            .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
            // Jos epäonnistuu, yritä uudelleen eksponentiaalisella viiveellä
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 10, TimeUnit.SECONDS)
            .build()

        workManager.enqueueUniqueWork(
            "delete_$storyId", // Yksilöllinen nimi estää duplikaatit
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