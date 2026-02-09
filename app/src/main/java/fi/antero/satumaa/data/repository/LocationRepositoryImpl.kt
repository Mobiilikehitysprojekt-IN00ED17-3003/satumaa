package fi.antero.satumaa.data.repository

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 * Sijaintipalveluiden toteutus käyttäen Google Play Services -kirjastoa (FusedLocationProvider).
 *
 * FusedLocationProvider on Googlen suosittelema tapa hakea sijainti, sillä se yhdistää
 * GPS:n, Wi-Fi:n ja mobiiliverkon tiedot parhaan ja akkuystävällisimmän tuloksen saamiseksi.
 */
class LocationRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : LocationRepository {

    // Googlen API-asiakas sijainnille
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    /**
     * Hakee sijainnin callbackFlow:n avulla.
     *
     * @SuppressLint("MissingPermission"):
     * Tämä metodi ei tarkista lupia itse. Kutsujan (ViewModel/UI) vastuulla on varmistaa,
     * että ACCESS_FINE_LOCATION tai ACCESS_COARSE_LOCATION on myönnetty.
     */
    @SuppressLint("MissingPermission")
    override fun getCurrentLocation(): Flow<Location?> = callbackFlow {
        // CancellationToken mahdollistaa haun perumisen, jos Flow suljetaan kesken kaiken
        val cts = com.google.android.gms.tasks.CancellationTokenSource()

        // Pyydetään korkeaa tarkkuutta (GPS), koska sovellus on "Satumaa" ja etäisyydet tärkeitä
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cts.token)
            .addOnSuccessListener { loc ->
                // Lähetetään sijainti (voi olla null, jos sijaintia ei saada)
                trySend(loc)
                // Suljetaan virta heti yhden arvon jälkeen (One-shot request)
                close()
            }
            .addOnFailureListener {
                // Virhetilanteessa lähetetään null
                trySend(null)
                close()
            }

        // Siivous: Jos coroutine perutaan, perutaan myös sijaintipyyntö
        awaitClose { cts.cancel() }
    }

    /**
     * Hakee sijainnin suoraviivaisesti coroutinella.
     *
     * Käyttää 'runCatching' -lohkoa virheiden (kuten puuttuvat luvat tai suljettu GPS) hallintaan.
     */
    @SuppressLint("MissingPermission")
    override suspend fun getSingleLocation(): Location? {
        val cts = com.google.android.gms.tasks.CancellationTokenSource()
        return runCatching {
            // .await() on Kotlinin laajennusfunktio Googlen Task-objektille -> muuttaa sen suspend-funktioksi
            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cts.token).await()
        }.getOrNull() // Palauttaa null, jos mikä tahansa menee pieleen
    }
}