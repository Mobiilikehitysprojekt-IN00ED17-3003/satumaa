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
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReplyNotificationWatcher @Inject constructor(
    private val auth: FirebaseAuth,
    private val letterRepository: LetterRepository
) : DefaultLifecycleObserver {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private var job: Job? = null
    private var started = false
    private lateinit var appContext: Context

    private var lastNotifiedLetterId: String? = null
    private val notifiedIds = linkedSetOf<String>()

    fun start(context: Context) {
        if (started) return
        started = true
        appContext = context.applicationContext
        NotificationHelper.ensureChannel(appContext)
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    override fun onStart(owner: LifecycleOwner) {
        if (job?.isActive == true) return
        job = scope.launch { run(appContext) }
    }

    override fun onStop(owner: LifecycleOwner) {
        job?.cancel()
        job = null
    }

    private suspend fun run(context: Context) = coroutineScope {
        var primed = false

        authUidFlow()
            .flatMapLatest { uid ->
                if (uid == null) {
                    lastNotifiedLetterId = null
                    notifiedIds.clear()
                    primed = false
                    flowOf(emptyList())
                } else {
                    letterRepository.getLetters()
                }
            }
            .collectLatest { letters ->
                val openedIds = letters.filter { it.isOpened }.map { it.id }.toSet()
                if (openedIds.isNotEmpty()) {
                    notifiedIds.removeAll(openedIds)
                    if (lastNotifiedLetterId in openedIds) lastNotifiedLetterId = null
                }

                val repliedUnopened = letters
                    .asSequence()
                    .filter { it.status == "replied" && !it.isOpened }
                    .sortedByDescending { it.createdAt?.toDate()?.time ?: 0L }
                    .toList()

                if (!primed) {
                    repliedUnopened.forEach { notifiedIds.add(it.id) }
                    primed = true
                    return@collectLatest
                }

                val candidate = repliedUnopened.firstOrNull() ?: return@collectLatest

                if (candidate.id == lastNotifiedLetterId) return@collectLatest
                if (candidate.id in notifiedIds) return@collectLatest

                lastNotifiedLetterId = candidate.id
                notifiedIds.add(candidate.id)

                // VÄLITETÄÄN ID TÄSSÄ:
                NotificationHelper.showSantaReplyNotification(context, candidate.id)
            }
    }

    private fun authUidFlow() = callbackFlow<String?> {
        val listener = FirebaseAuth.AuthStateListener { state ->
            trySend(state.currentUser?.uid)
        }
        auth.addAuthStateListener(listener)
        trySend(auth.currentUser?.uid)
        awaitClose { auth.removeAuthStateListener(listener) }
    }.distinctUntilChanged()
}