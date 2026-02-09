package fi.antero.satumaa.viewmodel.auth

import com.google.firebase.auth.FirebaseUser

/**
 * Määrittelee kirjautumisruudun mahdolliset tilat.
 * Sealed interface pakottaa käsittelemään kaikki tilat UI:ssa (when-lauseke).
 */
sealed interface AuthUiState {
    // Odottaa käyttäjän toimintoa
    object Idle : AuthUiState

    // Kirjautuminen käynnissä (näytä spinneri)
    object Loading : AuthUiState

    // Kirjautuminen onnistui (sisältää käyttäjätiedot)
    data class Success(val user: FirebaseUser) : AuthUiState

    // Kirjautuminen epäonnistui (sisältää virheilmoituksen)
    data class Error(val message: String) : AuthUiState
}