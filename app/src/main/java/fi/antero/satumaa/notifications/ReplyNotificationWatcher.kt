package fi.antero.satumaa.notifications

import android.content.Context
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.firebase.auth.FirebaseAuth
import fi.antero.satumaa.data.repository.LetterRepository
import fi.antero.satumaa.util.TravelTimeCalculator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
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

    fun start(context: Context) {
        if (started) return
        started = true

        appContext = context.applicationContext
        NotificationHelper.ensureChannel(appContext)

        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    override fun onStart(owner: LifecycleOwner) {
        if (job?.isActive == true) return
        job = scope.launch {
            run(appContext)
        }
    }

    override fun onStop(owner: LifecycleOwner) {
        job?.cancel()
        job = null
    }

    private suspend fun run(context: Context) = coroutineScope {
        var lastRepliedLetterId: String? = null
        var notifyJob: Job? = null

        authUidFlow()
            .flatMapLatest { uid ->
                if (uid == null) flowOf(emptyList())
                else letterRepository.getLetters()
            }
            .collectLatest { letters ->
                val newReply = letters.firstOrNull { it.status == "replied" && !it.isOpened }
                    ?: return@collectLatest

                if (newReply.id == lastRepliedLetterId) return@collectLatest
                lastRepliedLetterId = newReply.id

                notifyJob?.cancel()
                notifyJob = launch {
                    val createdAtMs = newReply.createdAt?.toDate()?.time ?: System.currentTimeMillis()
                    val deliveryTime = TravelTimeCalculator.getDeliveryTime(newReply.id, createdAtMs)
                    val delayMs = deliveryTime - System.currentTimeMillis()

                    val isTooOld = delayMs < -(5 * 60 * 1000)
                    if (!isTooOld) {
                        if (delayMs > 0) delay(delayMs)
                        NotificationHelper.showSantaReplyNotification(context)
                    }
                }
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
