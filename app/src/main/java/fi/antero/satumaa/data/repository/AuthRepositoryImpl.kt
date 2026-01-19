package fi.antero.satumaa.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth
) : AuthRepository {

    override val currentUser: FirebaseUser?
        get() = auth.currentUser

    override suspend fun signInWithGoogle(idToken: String): Result<FirebaseUser> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val authResult = auth.signInWithCredential(credential).await()
            val user = authResult.user
            if (user != null) Result.success(user) else Result.failure(Exception("User null"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- KORJATTU KOHTA: Lisätty 'override' sana ---
    override suspend fun signInAnonymously(): Result<FirebaseUser> {
        return try {
            val authResult = auth.signInAnonymously().await()
            val user = authResult.user
            if (user != null) {
                Result.success(user)
            } else {
                Result.failure(Exception("Anonyymi kirjautuminen epäonnistui"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun signOut() {
        auth.signOut()
    }
}