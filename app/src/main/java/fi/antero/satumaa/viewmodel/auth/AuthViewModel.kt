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

/**
 * AuthViewModel hallinnoi käyttäjän tunnistautumista.
 *
 * Tehtävät:
 * 1. Tarkistaa sovelluksen käynnistyessä, onko käyttäjä jo kirjautunut (Auto-login).
 * 2. Käsittelee anonyymin kirjautumisen.
 * 3. Käsittelee Google-kirjautumisen (ID-tokenin välitys).
 * 4. Muuntaa tekniset virheet (Exceptions) käyttäjäystävällisiksi viesteiksi.
 */
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository,
    // Context injektoidaan, jotta voimme hakea käännösresursseja (R.string)
    // virheviestien muodostamisessa (ks. ErrorUtils).
    @ApplicationContext private val context: Context
) : ViewModel() {

    // UI:n tila (Idle, Loading, Success, Error)
    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        // AUTO-LOGIN:
        // Kun ViewModel luodaan, tarkistetaan heti, onko käyttäjä välimuistissa.
        // Jos on, UI voi hypätä suoraan päävalikkoon ilman kirjautumisruutua.
        val currentUser = repository.currentUser
        if (currentUser != null) {
            _uiState.value = AuthUiState.Success(currentUser)
        }
    }

    /**
     * Kirjaudu sisään ilman tunnuksia (Firebase Anonymous Auth).
     * Kätevä "Kokeile heti" -toiminnallisuuteen.
     */
    fun signInAnonymously() {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading

            repository.signInAnonymously()
                .onSuccess { user ->
                    _uiState.value = AuthUiState.Success(user)
                }
                .onFailure { error ->
                    // Muunnetaan tekninen virhe (esim. "NETWORK_ERROR") selkokieliseksi
                    // resurssimerkkijonoksi käyttäen apufunktiota.
                    _uiState.value = AuthUiState.Error(error.toUserFriendlyMessage(context))
                }
        }
    }

    /**
     * Kirjaudu sisään Google-tunnuksilla.
     * @param idToken Googlen API:lta saatu vahvistustoken, joka lähetetään Firebaselle.
     */
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

    /**
     * Kirjaudu ulos ja nollaa tila.
     */
    fun signOut() {
        repository.signOut()
        _uiState.value = AuthUiState.Idle
    }

    /**
     * Nollaa virhetilan (esim. kun käyttäjä painaa "OK" virhe-dialogissa).
     */
    fun resetState() {
        _uiState.value = AuthUiState.Idle
    }
}