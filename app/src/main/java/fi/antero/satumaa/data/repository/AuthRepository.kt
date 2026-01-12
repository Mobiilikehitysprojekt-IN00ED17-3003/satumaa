package fi.antero.satumaa.data.repository

import com.google.firebase.auth.FirebaseUser

interface AuthRepository {
    val currentUser: FirebaseUser?
    suspend fun signInWithGoogle(idToken: String): Result<FirebaseUser>
    fun signOut()
}