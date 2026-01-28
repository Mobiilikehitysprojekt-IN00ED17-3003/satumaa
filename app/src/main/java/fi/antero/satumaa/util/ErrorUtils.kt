package fi.antero.satumaa.util

import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import java.net.UnknownHostException
import java.util.concurrent.TimeoutException

/**
 * Muuntaa tekniset virhekoodit ja poikkeukset ymmärrettäväksi suomenkieliseksi tekstiksi.
 * Tämä toimii sovelluksen keskitettynä "sanakirjana".
 */
fun Throwable.toUserFriendlyMessage(): String {
    // 1. Fyysiset verkkovirheet (laitetaso)
    if (this is UnknownHostException || this is TimeoutException) {
        return "Yhteysongelma. Tarkista internetyhteytesi."
    }

    // Normalisoidaan viesti vertailua varten (muutetaan isoiksi kirjaimiksi)
    val msg = this.message?.uppercase() ?: ""

    return when {
        // --- TARINA-SPESIFISET KOODIT (StoryViewModel & Functions) ---
        msg.contains("STORY_KEYWORDS_EMPTY") ->
            "Kirjoita ainakin yksi taikasana!"
        msg.contains("STORY_NOT_FOUND") ->
            "Hups! Satua ei löytynyt kirjastosta."
        msg.contains("EMPTY_RESPONSE") ->
            "Taikuus epäonnistui: Palvelin ei palauttanut satua."
        msg.contains("SAVE_ID_MISSING") ->
            "Satu tallentui pilveen, mutta sen tunnistetta ei saatu."

        // --- REPOSITORYN VERKKOVIRHE-KOODI ---
        msg.contains("NETWORK_ERROR") ->
            "Verkkovirhe. Tarkista, että puhelimesi on yhdistetty internetiin."

        // --- GOOGLE-KIRJAUTUMINEN (LoginScreen) ---
        msg.contains("AUTH_GOOGLE_TOKEN_MISSING") ->
            "Google-kirjautuminen epäonnistui: Tunniste puuttuu."
        msg.contains("AUTH_GOOGLE_API_ERROR") ->
            "Yhteys Google-tiliin epäonnistui. Yritä uudelleen."

        // --- AUTH REPOSITORY (AuthRepositoryImpl) ---
        msg.contains("AUTH_USER_NULL") ->
            "Kirjautuminen epäonnistui: Käyttäjätietoja ei saatu."
        msg.contains("AUTH_ANONYMOUS_FAILED") ->
            "Pikakirjautuminen epäonnistui. Kokeile uudelleen."

        // --- TARINAT (Backend & Repo) ---
        msg.contains("RATE_LIMIT_STORY") -> "Voit taikoa vain yhden sadun minuutissa. Odota hetki."
        msg.contains("RATE_LIMIT_SAVE") -> "Tallennat liian nopeasti. Odota hetki."
        msg.contains("STORAGE_FULL") -> "Kirjahylly on täynnä (20/20). Poista vanhoja satuja ensin."
        msg.contains("PREVIEW_EXPIRED") -> "Esikatselu vanhentui. Luo satu uudelleen."
        msg.contains("CONTENT_MISMATCH") -> "Satu muuttui matkalla. Yritä tallennusta uudelleen."
        msg.contains("INVALID_INPUT") -> "Tarkista syöttämäsi nimi tai aiheet."

        // --- KIRJEET (Backend & Repo) ---
        msg.contains("MAILBOX_FULL") -> "Postilaatikko on täynnä (10/10). Poista vanhoja kirjeitä ensin."
        msg.contains("LETTER_TOO_LONG") -> "Kirje on liian pitkä. Lyhennä sitä hieman."
        msg.contains("RATE_LIMIT_LETTER") -> "Pukki ehtii lukea vain yhden kirjeen minuutissa."
        msg.contains("GEMINI_BUSY") -> "Pukilla on kova kiire juuri nyt. Yritä hetken päästä uudelleen."

        // --- FIREBASE AUTH (Firebase omat poikkeukset) ---
        this is FirebaseAuthInvalidCredentialsException -> "Virheellinen sähköposti tai salasana."
        this is FirebaseAuthInvalidUserException -> "Käyttäjätiliä ei löydy tai se on poistettu."
        this is FirebaseAuthUserCollisionException -> "Tällä sähköpostilla on jo tili."
        this is FirebaseAuthWeakPasswordException -> "Salasana on liian heikko."
        msg.contains("BLOCKED") -> "Tili on estetty väliaikaisesti liian monen yrityksen vuoksi."

        // --- MUUT FIREBASE/YLEISET ---
        msg.contains("TIMEOUT") || msg.contains("DEADLINE_EXCEEDED") ->
            "Pyyntö aikakatkaistiin. Palvelin vastaa hitaasti."

        msg.contains("UNAUTHENTICATED") || msg.contains("AUTH_REQUIRED") || msg.contains("PERMISSION_DENIED") ->
            "Toiminto vaatii kirjautumisen."

        // --- OLETUS ---
        else -> "Jotain meni pieleen. Yritä uudelleen."
    }
}

/**
 * Apufunktio, jolla voidaan kääntää pelkkä merkkijono (esim. Firestoren errorMessage-kenttä).
 */
fun String?.mapErrorToUserMessage(): String {
    if (this.isNullOrBlank()) return "Tuntematon virhe."
    return Exception(this).toUserFriendlyMessage()
}