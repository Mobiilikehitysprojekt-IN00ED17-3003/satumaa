package fi.antero.satumaa.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 * Autentikaation toteutus käyttäen Firebase Authentication -palvelua.
 */
class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth
) : AuthRepository {

    // Välitetään suoraan Firebasen nykyinen käyttäjä
    override val currentUser: FirebaseUser?
        get() = auth.currentUser

    /**
     * Google-kirjautumisen logiikka.
     * Käyttää 'GoogleAuthProvider':ia luomaan credential-objektin tokenista.
     */
    override suspend fun signInWithGoogle(idToken: String): Result<FirebaseUser> {
        return try {
            // Luodaan Firebase-credential Googlen tokenista
            val credential = GoogleAuthProvider.getCredential(idToken, null)

            // Suoritetaan varsinainen kirjautuminen (await odottaa coroutinessa)
            val authResult = auth.signInWithCredential(credential).await()
            val user = authResult.user

            if (user != null) {
                Result.success(user)
            } else {
                // Heitetään tekninen virhekoodi, jonka ErrorUtils kääntää myöhemmin
                Result.failure(Exception("AUTH_USER_NULL"))
            }
        } catch (e: Exception) {
            // Kaikki poikkeukset (verkkovirheet, väärät tokenit) palautetaan Result.failurena
            Result.failure(e)
        }
    }

    /**
     * Anonyymin kirjautumisen logiikka.
     * Hyödyllinen "kokeile sovellusta" -ominaisuudelle ilman rekisteröitymistä.
     */
    override suspend fun signInAnonymously(): Result<FirebaseUser> {
        return try {
            val authResult = auth.signInAnonymously().await()
            val user = authResult.user

            if (user != null) {
                Result.success(user)
            } else {
                Result.failure(Exception("AUTH_ANONYMOUS_FAILED"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun signOut() {
        auth.signOut()
    }
}