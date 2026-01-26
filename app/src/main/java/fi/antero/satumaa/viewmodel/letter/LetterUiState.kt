package fi.antero.satumaa.viewmodel.letter

import android.location.Location

data class LetterUiState(
    // Mitä käyttäjä kirjoittaa tekstikenttään
    val text: String = "",

    // True kun lähetystä ollaan käynnistämässä / odotetaan repo-vastausta
    val isSending: Boolean = false,


     // Kirjeen "tila"

    val status: String? = null,

    // Pukin vastausteksti (kun status == "replied")
    val replyText: String? = null,

    // Virheviesti UI:lle (näytetään ErrorView:ssä)
    val error: String? = null,

    // True jos käytettiin offline-demoa (eli backend ei ollut käytössä)
    val usedOfflineDemo: Boolean = false,


    val showReplyArrived: Boolean = false,

    // Kartta
    val userLocation: Location? = null,
    val distanceToSantaKm: Double? = null,
    val isLocating: Boolean = false,
    val hasLocationPermission: Boolean = false
)
