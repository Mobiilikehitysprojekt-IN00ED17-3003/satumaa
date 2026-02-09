package fi.antero.satumaa.viewmodel.auth

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import fi.antero.satumaa.data.repository.AuthRepository
import fi.antero.satumaa.util.toUserFriendlyMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        // Vain kevyt tarkistus onko käyttäjä jo sisällä
        val currentUser = repository.currentUser
        if (currentUser != null) {
            _uiState.value = AuthUiState.Success(currentUser)
        }
    }

    fun signInAnonymously() {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            repository.signInAnonymously()
                .onSuccess { user ->
                    _uiState.value = AuthUiState.Success(user)
                }
                .onFailure { error ->
                    _uiState.value = AuthUiState.Error(error.toUserFriendlyMessage(context))
                }
        }
    }

    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            repository.signInWithGoogle(idToken)
                .onSuccess { user ->
                    _uiState.value = AuthUiState.Success(user)
                }
                .onFailure { error ->
                    Log.e("AuthViewModel", "Google Sign-In failed: ${error.message}")
                    _uiState.value = AuthUiState.Error(error.toUserFriendlyMessage(context))
                }
        }
    }

    fun signOut() {
        repository.signOut()
        _uiState.value = AuthUiState.Idle
    }

    fun resetState() {
        _uiState.value = AuthUiState.Idle
    }
}