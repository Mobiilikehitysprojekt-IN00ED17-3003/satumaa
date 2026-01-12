package fi.antero.satumaa.viewmodel.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fi.antero.satumaa.data.repository.AuthRepository
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
            _uiState.value = AuthUiState.Success(currentUser)
        }
    }

    fun signInWithGoogle(idToken: String) {

        _uiState.value = AuthUiState.Loading

        viewModelScope.launch {

            val result = repository.signInWithGoogle(idToken)


            result.onSuccess { user ->
                _uiState.value = AuthUiState.Success(user)
            }.onFailure { error ->
                _uiState.value = AuthUiState.Error(error.localizedMessage ?: "Tuntematon virhe")
            }
        }
    }

    fun signOut() {
        repository.signOut()
        _uiState.value = AuthUiState.Idle
    }
}