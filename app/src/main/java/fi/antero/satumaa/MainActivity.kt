package fi.antero.satumaa

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import dagger.hilt.android.AndroidEntryPoint
import fi.antero.satumaa.notifications.NotificationHelper
import fi.antero.satumaa.notifications.ReplyNotificationWatcher
import fi.antero.satumaa.ui.SatumaaApp
import fi.antero.satumaa.ui.theme.SatumaaTheme
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var replyNotificationWatcher: ReplyNotificationWatcher

    // Tila, joka säilyttää tiedon notifikaatiosta tulleesta kirjeen ID:stä
    // Tämä on State, jotta Compose huomaa muutoksen heti
    private var launchedLetterId by mutableStateOf<String?>(null)

    private val requestNotificationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Tarkistetaan heti käynnistyksessä
        handleIntent(intent)

        requestNotificationPermissionIfNeeded()
        replyNotificationWatcher.start(this)

        setContent {
            SatumaaTheme {
                // Välitetään tieto ja callback nollausta varten
                SatumaaApp(
                    launchedLetterId = launchedLetterId,
                    onNavigationHandled = { launchedLetterId = null }
                )
            }
        }
    }

    // 2. Tätä kutsutaan, jos sovellus on jo taustalla auki
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        // TÄRKEÄ: Päivitetään aktiviteetin intent, jotta myöhemmät kyselyt saavat uuden datan
        setIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        if (intent == null) return

        val letterId = intent.getStringExtra(NotificationHelper.EXTRA_LETTER_ID)
        if (!letterId.isNullOrEmpty()) {
            launchedLetterId = letterId
        }
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return

        val granted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED

        if (!granted) {
            requestNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}