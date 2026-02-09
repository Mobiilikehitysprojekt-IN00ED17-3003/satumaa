package fi.antero.satumaa.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.core.content.ContextCompat

/**
 * Apuluokka Androidin lupien ja järjestelmäasetusten tarkistamiseen.
 * Eristää Android-spesifisen koodin pois ViewModelista/UI:sta.
 */
object PermissionUtils {

    /**
     * Tarkistaa, onko sovelluksella lupa käyttää tarkkaa sijaintia (GPS).
     */
    fun hasFineLocationPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Tarkistaa, onko puhelimen sijaintipalvelut (GPS tai Verkko) päällä asetuksista.
     * Vaikka lupa olisi myönnetty, käyttäjä voi olla sammuttanut GPS:n kokonaan.
     */
    fun isLocationEnabled(context: Context): Boolean {
        val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        // Käytetään runCatching siltä varalta, että jokin heittää poikkeuksen oudoilla laitteilla
        val gps = runCatching { lm.isProviderEnabled(LocationManager.GPS_PROVIDER) }.getOrDefault(false)
        val net = runCatching { lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER) }.getOrDefault(false)

        return gps || net
    }
}