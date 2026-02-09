package fi.antero.satumaa.notifications

import android.content.Context
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.firebase.auth.FirebaseAuth
import fi.antero.satumaa.data.repository.LetterRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Taustapalvelu (Observer), joka tarkkailee kirjeiden tilaa ja lähettää ilmoituksen,
 * kun uusi vastaus saapuu.
 *
 * Toimintaperiaate:
 * 1. Tarkkailee kirjautunutta käyttäjää (Auth).
 * 2. Kun käyttäjä on kirjautunut, tarkkailee kirjeitä (Repository Flow).
 * 3. Kun kirjeen tila muuttuu "replied" ja sitä ei ole avattu -> Ilmoitus.
 *
 * Tämä luokka on sidottu sovelluksen prosessin elinkaareen (ProcessLifecycleOwner),
 * joten se toimii niin kauan kuin sovellus on muistissa (myös taustalla).
 */
@Singleton
class ReplyNotificationWatcher @Inject constructor(
    private val auth: FirebaseAuth,
    private val letterRepository: LetterRepository
) : DefaultLifecycleObserver {

    // Oma scope, joka ei kuole vaikka Activity tuhoutuisi (koska Singleton)
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    private var job: Job? = null
    private var started = false
    private lateinit var appContext: Context

    // Seurataan tiloja, jotta emme lähetä ilmoitusta samasta kirjeestä kahdesti
    private var lastNotifiedLetterId: String? = null
    private val notifiedIds = linkedSetOf<String>()

    /**
     * Käynnistää tarkkailijan. Kutsutaan Application-luokassa tai MainActivityssä kerran.
     */
    fun start(context: Context) {
        if (started) return
        started = true
        appContext = context.applicationContext

        // Varmistetaan kanava heti alussa
        NotificationHelper.ensureChannel(appContext)

        // Sidotaan sovelluksen elinkaareen
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    // Kun sovellus käynnistyy (tai tulee foregroundiin ensimmäistä kertaa)
    override fun onStart(owner: LifecycleOwner) {
        if (job?.isActive == true) return
        job = scope.launch { run(appContext) }
    }

    // Kun sovellus sammuu kokonaan (tai prosessi tapetaan)
    override fun onStop(owner: LifecycleOwner) {
        job?.cancel()
        job = null
    }

    /**
     * Pääsilmukka, joka tarkkailee datavirtoja.
     */
    private suspend fun run(context: Context) {
        // 'primed' estää vanhojen ilmoitusten tulvan käynnistyksessä.
        // Kun sovellus käynnistyy, emme halua ilmoittaa kaikista vanhoista vastaamattomista kirjeistä,
        // vaan ainoastaan niistä, jotka saapuvat *tämän jälkeen*.
        var primed = false

        authUidFlow()
            .flatMapLatest { uid ->
                if (uid == null) {
                    // Jos käyttäjä kirjautuu ulos, nollataan tilat
                    lastNotifiedLetterId = null
                    notifiedIds.clear()
                    primed = false
                    flowOf(emptyList()) // Lopetetaan kirjeiden kuuntelu
                } else {
                    // Jos kirjautunut, aletaan kuunnella kirjeitä
                    letterRepository.getLetters()
                }
            }
            .collectLatest { letters ->
                // 1. Poistetaan "notified"-listalta ne, jotka käyttäjä on nyt avannut
                val openedIds = letters.filter { it.isOpened }.map { it.id }.toSet()
                if (openedIds.isNotEmpty()) {
                    notifiedIds.removeAll(openedIds)
                    if (lastNotifiedLetterId in openedIds) lastNotifiedLetterId = null
                }

                // 2. Etsitään ehdokkaat ilmoitukselle:
                // Status = "replied" JA EI ole avattu
                val repliedUnopened = letters
                    .asSequence()
                    .filter { it.status == "replied" && !it.isOpened }
                    .sortedByDescending { it.createdAt?.toDate()?.time ?: 0L }
                    .toList()

                // 3. Ensimmäinen ajokerta (sovelluksen käynnistys):
                // Merkitään kaikki nykyiset "nähdyiksi" tässä sessiossa, ettei niistä tule ilmoitusta.
                if (!primed) {
                    repliedUnopened.forEach { notifiedIds.add(it.id) }
                    primed = true
                    return@collectLatest
                }

                // 4. Tarkistetaan uusin ehdokas
                val candidate = repliedUnopened.firstOrNull() ?: return@collectLatest

                // Jos tästä on jo ilmoitettu, ei tehdä mitään
                if (candidate.id == lastNotifiedLetterId) return@collectLatest
                if (candidate.id in notifiedIds) return@collectLatest

                // 5. Uusi vastaus havaittu -> Näytä ilmoitus!
                lastNotifiedLetterId = candidate.id
                notifiedIds.add(candidate.id)

                NotificationHelper.showSantaReplyNotification(context, candidate.id)
            }
    }

    /**
     * Luo Flow'n, joka emittoi käyttäjän ID:n aina kun auth-tila muuttuu.
     */
    private fun authUidFlow() = callbackFlow<String?> {
        val listener = FirebaseAuth.AuthStateListener { state ->
            trySend(state.currentUser?.uid)
        }
        auth.addAuthStateListener(listener)
        trySend(auth.currentUser?.uid)
        awaitClose { auth.removeAuthStateListener(listener) }
    }.distinctUntilChanged()
}