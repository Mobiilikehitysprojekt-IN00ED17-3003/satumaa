package fi.antero.satumaa.data.repository

import android.location.Location
import kotlinx.coroutines.flow.Flow

/**
 * Rajapinta laitteen sijainnin hakemiseen.
 *
 * Eristää Google Play Services -riippuvuuden muusta sovelluksesta.
 * Tätä käytetään esimerkiksi kirjeen lähetysmatkan visualisointiin tai
 * etäisyyden laskemiseen Joulupukin pajalle.
 */
interface LocationRepository {

    /**
     * Hakee nykyisen sijainnin reaktiivisena virtana (Flow).
     * Hyödyllinen, jos UI haluaa reagoida sijainnin valmistumiseen
     * Flow-putkessa (esim. combine-operaattorilla).
     */
    fun getCurrentLocation(): Flow<Location?>

    /**
     * Hakee nykyisen sijainnin kertaalleen (Suspend function).
     * Tämä on yksinkertaisempi tapa hakea sijainti esimerkiksi napin painalluksesta.
     */
    suspend fun getSingleLocation(): Location?
}