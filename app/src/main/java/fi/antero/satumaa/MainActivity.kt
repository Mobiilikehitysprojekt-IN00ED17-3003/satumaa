package fi.antero.satumaa

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import fi.antero.satumaa.data.repository.LetterRepository
import fi.antero.satumaa.notifications.NotificationHelper
import fi.antero.satumaa.ui.SatumaaApp
import fi.antero.satumaa.ui.theme.SatumaaTheme
import fi.antero.satumaa.util.TravelTimeCalculator
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var letterRepository: LetterRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        NotificationHelper.ensureChannel(this)

        lifecycleScope.launch {
            var lastRepliedLetterId: String? = null

            letterRepository.getLetters().collectLatest { letters ->
                // Etsitään kirje, joka on "replied" (valmis) mutta EI vielä avattu
                val newReply = letters.firstOrNull {
                    it.status == "replied" && !it.isOpened
                }

                if (newReply != null && newReply.id != lastRepliedLetterId) {
                    lastRepliedLetterId = newReply.id

                    val createdAtMs = newReply.createdAt?.toDate()?.time ?: System.currentTimeMillis()

                    // Lasketaan toimitusaika samalla logiikalla kuin muuallakin
                    val deliveryTime = TravelTimeCalculator.getDeliveryTime(newReply.id, createdAtMs)
                    val now = System.currentTimeMillis()
                    val delayMs = deliveryTime - now

                    // Jos kirje on "ikivanha" (yli 5 min myöhässä), ei notifioida
                    val isTooOld = delayMs < -(5 * 60 * 1000)

                    if (!isTooOld) {
                        lifecycleScope.launch {
                            if (delayMs > 0) {
                                delay(delayMs)
                            }
                            NotificationHelper.showSantaReplyNotification(this@MainActivity)
                        }
                    }
                }
            }
        }

        setContent {
            SatumaaTheme {
                SatumaaApp()
            }
        }
    }
}