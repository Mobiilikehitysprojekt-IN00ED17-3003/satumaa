package fi.antero.satumaa.viewmodel.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fi.antero.satumaa.data.repository.AuthRepository
import fi.antero.satumaa.util.toUserFriendlyMessage // UUSI IMPORT
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        val currentUser = repository.currentUser
        if (currentUser != null) {
            Log.e("TOKEN_DEBUG", "Käyttäjä valmiiksi kirjautunut: ${currentUser.email}")
            currentUser.getIdToken(true).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.e("TOKEN_DEBUG", "VANHA TOKEN: " + (task.result?.token ?: "null"))
                }
            }
            _uiState.value = AuthUiState.Success(currentUser)
        } else {
            Log.e("TOKEN_DEBUG", "Ei valmiiksi kirjautunutta käyttäjää.")
        }
    }

    fun signInAnonymously() {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading

            val result = repository.signInAnonymously()

            result.onSuccess { user ->
                _uiState.value = AuthUiState.Success(user)
            }.onFailure { error ->

                _uiState.value = AuthUiState.Error(error.toUserFriendlyMessage())
            }
        }
    }

    fun signInWithGoogle(idToken: String) {
        Log.e("TOKEN_DEBUG", "signInWithGoogle kutsuttu! ID Tokenin pituus: ${idToken.length}")
        _uiState.value = AuthUiState.Loading

        viewModelScope.launch {
            val result = repository.signInWithGoogle(idToken)

            result.onSuccess { user ->
                Log.e("TOKEN_DEBUG", "Repository palautti Success!")
                user.getIdToken(true).addOnCompleteListener { task ->
                    val token = task.result?.token
                    Log.e("TOKEN_DEBUG", "========================================")
                    Log.e("TOKEN_DEBUG", "UUSI TOKEN: $token")
                    Log.e("TOKEN_DEBUG", "========================================")
                }
                _uiState.value = AuthUiState.Success(user)
            }.onFailure { error ->
                Log.e("TOKEN_DEBUG", "Kirjautuminen epäonnistui: ${error.message}")

                _uiState.value = AuthUiState.Error(error.toUserFriendlyMessage())
            }
        }
    }

    fun signOut() {
        Log.e("TOKEN_DEBUG", "Kirjaudutaan ulos.")
        repository.signOut()
        _uiState.value = AuthUiState.Idle
    }

    fun resetState() {
        _uiState.value = AuthUiState.Idle
    }
}