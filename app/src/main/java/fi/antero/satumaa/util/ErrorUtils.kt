package fi.antero.satumaa.util

import android.content.Context
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import fi.antero.satumaa.R
import java.net.UnknownHostException
import java.util.concurrent.TimeoutException

/**
 * Muuntaa tekniset virhekoodit ja poikkeukset ymmärrettäväksi tekstiksi.
 * Tämä toimii sovelluksen keskitettynä "sanakirjana".
 *
 * @param context Tarvitaan string-resurssien hakemiseen (R.string...).
 */
fun Throwable.toUserFriendlyMessage(context: Context): String {
    // 1. Fyysiset verkkovirheet (laitetaso)
    if (this is UnknownHostException || this is TimeoutException) {
        return context.getString(R.string.error_connection)
    }

    // Normalisoidaan viesti vertailua varten (muutetaan isoiksi kirjaimiksi)
    val msg = this.message?.uppercase() ?: ""

    return when {
        // --- TARINA-SPESIFISET KOODIT (StoryViewModel & Functions) ---
        msg.contains("STORY_KEYWORDS_EMPTY") -> context.getString(R.string.error_story_keywords_empty)
        msg.contains("STORY_NOT_FOUND") -> context.getString(R.string.error_story_not_found)
        msg.contains("EMPTY_RESPONSE") -> context.getString(R.string.error_empty_response)
        msg.contains("SAVE_ID_MISSING") -> context.getString(R.string.error_save_id_missing)

        // --- REPOSITORYN VERKKOVIRHE-KOODI ---
        msg.contains("NETWORK_ERROR") -> context.getString(R.string.error_network_generic)

        // --- GOOGLE-KIRJAUTUMINEN (LoginScreen) ---
        msg.contains("AUTH_GOOGLE_TOKEN_MISSING") -> context.getString(R.string.error_auth_google_token)
        msg.contains("AUTH_GOOGLE_API_ERROR") -> context.getString(R.string.error_auth_google_api)

        // --- AUTH REPOSITORY (AuthRepositoryImpl) ---
        msg.contains("AUTH_USER_NULL") -> context.getString(R.string.error_auth_user_null)
        msg.contains("AUTH_ANONYMOUS_FAILED") -> context.getString(R.string.error_auth_anonymous)

        // --- TARINAT (Backend & Repo) ---
        msg.contains("RATE_LIMIT_STORY") -> context.getString(R.string.error_rate_limit_story)
        msg.contains("RATE_LIMIT_SAVE") -> context.getString(R.string.error_rate_limit_save)
        msg.contains("STORAGE_FULL") -> context.getString(R.string.error_storage_full)
        msg.contains("PREVIEW_EXPIRED") -> context.getString(R.string.error_preview_expired)
        msg.contains("CONTENT_MISMATCH") -> context.getString(R.string.error_content_mismatch)
        msg.contains("INVALID_INPUT") -> context.getString(R.string.error_invalid_input)

        // --- KIRJEET (Backend & Repo) ---
        msg.contains("MAILBOX_FULL") -> context.getString(R.string.error_mailbox_full)
        msg.contains("LETTER_TOO_LONG") -> context.getString(R.string.error_letter_too_long)
        msg.contains("RATE_LIMIT_LETTER") -> context.getString(R.string.error_rate_limit_letter)
        msg.contains("GEMINI_BUSY") -> context.getString(R.string.error_gemini_busy)

        // --- FIREBASE AUTH (Firebase omat poikkeukset) ---
        this is FirebaseAuthInvalidCredentialsException -> context.getString(R.string.error_auth_invalid_cred)
        this is FirebaseAuthInvalidUserException -> context.getString(R.string.error_auth_invalid_user)
        this is FirebaseAuthUserCollisionException -> context.getString(R.string.error_auth_collision)
        this is FirebaseAuthWeakPasswordException -> context.getString(R.string.error_auth_weak_password)
        msg.contains("BLOCKED") -> context.getString(R.string.error_auth_blocked)

        // --- MUUT FIREBASE/YLEISET ---
        msg.contains("TIMEOUT") || msg.contains("DEADLINE_EXCEEDED") -> context.getString(R.string.error_timeout)
        msg.contains("UNAUTHENTICATED") || msg.contains("AUTH_REQUIRED") || msg.contains("PERMISSION_DENIED") -> context.getString(R.string.error_auth_required)

        // --- OLETUS ---
        else -> context.getString(R.string.error_generic)
    }
}

/**
 * Apufunktio, jolla voidaan kääntää pelkkä merkkijono (esim. Firestoren errorMessage-kenttä).
 */
fun String?.mapErrorToUserMessage(context: Context): String {
    if (this.isNullOrBlank()) return context.getString(R.string.error_unknown)
    return Exception(this).toUserFriendlyMessage(context)
}