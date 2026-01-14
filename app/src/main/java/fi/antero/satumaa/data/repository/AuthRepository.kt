package fi.antero.satumaa.data.repository

import com.google.firebase.auth.FirebaseUser

interface AuthRepository {
    val currentUser: FirebaseUser?
    suspend fun signInWithGoogle(idToken: String): Result<FirebaseUser>

    suspend fun signInAnonymously(): Result<FirebaseUser>
    // -----------------------

    fun signOut()
}