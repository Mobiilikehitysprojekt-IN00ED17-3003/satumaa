package fi.antero.satumaa

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import dagger.hilt.android.HiltAndroidApp
import fi.antero.satumaa.notifications.NotificationHelper
import org.osmdroid.config.Configuration as OsmConfig
import javax.inject.Inject

/**
 * Sovelluksen pääluokka.
 *
 * @HiltAndroidApp: Tämä anotaatio on pakollinen. Se käynnistää Hiltin koodingeneroinnin
 * ja luo sovellustason riippuvuussäiliön (Application Component).
 *
 * Configuration.Provider: Mahdollistaa Hiltin käytön WorkManagerin workereissa (esim. SyncWorker).
 */
@HiltAndroidApp
class SatumaaApplication : Application(), Configuration.Provider {

    // Injektoidaan Hiltin luoma tehdas, joka osaa luoda Workereita riippuvuuksineen.
    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    // Määritellään WorkManager käyttämään Hiltin tehdasta oletuksen sijaan.
    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()

        // 1. Alustetaan Firebase
        FirebaseApp.initializeApp(this)

        // 2. Konfiguroidaan App Check (Tietoturva)
        // Tämä estää luvatonta käyttöä backend-rajapinnoissa.
        val appCheck = FirebaseAppCheck.getInstance()
        if (BuildConfig.DEBUG) {
            // Kehityksessä käytetään Debug-provideria (tulostaa tokenin Logcatiin)
            appCheck.installAppCheckProviderFactory(DebugAppCheckProviderFactory.getInstance())
        } else {
            // Tuotannossa käytetään Play Integrity API:ta (varmistaa että sovellus on aito)
            appCheck.installAppCheckProviderFactory(PlayIntegrityAppCheckProviderFactory.getInstance())
        }

        // 3. Luodaan ilmoituskanavat valmiiksi (Android 8.0+ vaatimus)
        NotificationHelper.ensureChannel(this)

        // 4. Konfiguroidaan OpenStreetMap (osmdroid)
        // User-agent on pakollinen, jotta karttatiilet latautuvat OSM-palvelimilta.
        OsmConfig.getInstance().userAgentValue = packageName
    }
}