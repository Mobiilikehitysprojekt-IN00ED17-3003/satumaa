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

/**
 * Sovelluksen pääaktiviteetti.
 *
 * @AndroidEntryPoint on pakollinen, jotta Hilt voi injektoida riippuvuuksia (kuten ViewModelit
 * navigointigraafissa ja ReplyNotificationWatcher tässä luokassa).
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    // Injektoidaan taustapalvelu, joka tarkkailee saapuvia vastauksia.
    // Hilt hoitaa tämän luonnin.
    @Inject
    lateinit var replyNotificationWatcher: ReplyNotificationWatcher

    // Tila, joka säilyttää tiedon notifikaatiosta tulleesta kirjeen ID:stä.
    // Käytämme tässä Compose-statea (mutableStateOf), jotta muutos välittyy
    // reaktiivisesti SatumaaApp-komponentille, joka voi sitten navigoida oikeaan ruutuun.
    private var launchedLetterId by mutableStateOf<String?>(null)

    // Rekisteröidään lupapyyntöjen käsittelijä (Android 13+ ilmoituslupa)
    private val requestNotificationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            // Tässä voisi lokittaa tai reagoida, jos lupa evättiin
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Tarkistetaan heti käynnistyksessä, avattiinko sovellus ilmoituksesta
        handleIntent(intent)

        // 2. Pyydetään tarvittavat luvat
        requestNotificationPermissionIfNeeded()

        // 3. Käynnistetään taustakuuntelija (ProcessLifecycleOwner)
        replyNotificationWatcher.start(this)

        setContent {
            SatumaaTheme {
                // Käynnistetään varsinainen UI.
                // Välitetään 'launchedLetterId', jotta navigaatio osaa hypätä suoraan kirjeeseen.
                // 'onNavigationHandled' nollaa ID:n, jotta emme navigoi sinne uudestaan esim. rotaatiossa.
                SatumaaApp(
                    launchedLetterId = launchedLetterId,
                    onNavigationHandled = { launchedLetterId = null }
                )
            }
        }
    }

    /**
     * Tätä metodia kutsutaan, jos sovellus on jo taustalla auki ja käyttäjä klikkaa ilmoitusta.
     * Koska Activity on määritelty 'singleTop' tilassa (NotificationHelperissa),
     * järjestelmä ei luo uutta Activityä vaan kutsuu tätä.
     */
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        // TÄRKEÄ: Päivitetään aktiviteetin sisäinen intent-viittaus uuteen.
        // Jos tätä ei tee, 'getIntent()' palauttaa aina alkuperäisen käynnistys-intentin.
        setIntent(intent)

        // Käsitellään uusi intent (eli poimitaan kirjeen ID)
        handleIntent(intent)
    }

    /**
     * Purkaa Intentistä mahdollisen kirjeen ID:n.
     */
    private fun handleIntent(intent: Intent?) {
        if (intent == null) return

        val letterId = intent.getStringExtra(NotificationHelper.EXTRA_LETTER_ID)
        if (!letterId.isNullOrEmpty()) {
            // Päivitetään Compose-tila -> SatumaaApp reagoi ja navigoi
            launchedLetterId = letterId
        }
    }

    /**
     * Tarkistaa ja pyytää ilmoitusluvan Android 13 (API 33) ja uudemmissa.
     */
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