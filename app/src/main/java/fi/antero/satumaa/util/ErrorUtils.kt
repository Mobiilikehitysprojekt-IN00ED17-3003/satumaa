package fi.antero.satumaa.util

import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import java.net.UnknownHostException
import java.util.concurrent.TimeoutException

/**
 * Muuntaa tekniset virheet (Throwable) ymmärrettäväksi suomenkieliseksi tekstiksi.
 */
fun Throwable.toUserFriendlyMessage(): String {
    // 1. Verkkovirheet
    if (this is UnknownHostException || this is TimeoutException) {
        return "Yhteysongelma. Tarkista internetyhteytesi."
    }

    val msg = this.message?.lowercase() ?: ""

    return when {
        // Yleiset verkkosanat viestissä
        msg.contains("network") || msg.contains("connection") || msg.contains("offline") ->
            "Verkkovirhe. Oletko yhteydessä internetiin?"

        msg.contains("timeout") || msg.contains("deadline exceeded") ->
            "Pyyntö aikakatkaistiin. Palvelin vastaa hitaasti."

        // --- Firebase Auth ---
        this is FirebaseAuthInvalidCredentialsException ->
            "Virheellinen sähköposti tai salasana."

        this is FirebaseAuthInvalidUserException ->
            "Käyttäjätiliä ei löydy tai se on poistettu."

        this is FirebaseAuthUserCollisionException ->
            "Tällä sähköpostilla on jo tili."

        this is FirebaseAuthWeakPasswordException ->
            "Salasana on liian heikko."

        msg.contains("blocked") ->
            "Tili on väliaikaisesti estetty liian monen yrityksen vuoksi."

        // --- Cloud Functions / Generointi ---
        msg.contains("quota") ->
            "Palvelu on hetkellisesti ruuhkautunut (kiintiö täynnä). Kokeile myöhemmin."

        msg.contains("internal") ->
            "Palvelinvirhe. Yritä hetken päästä uudelleen."

        // --- Oletus ---
        else -> "Jotain meni pieleen. Yritä uudelleen."
    }
}