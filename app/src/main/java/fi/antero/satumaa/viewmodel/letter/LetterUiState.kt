package fi.antero.satumaa.viewmodel.letter

import android.location.Location

data class LetterUiState(
    // Letter related state
    val text: String = "",
    val isSending: Boolean = false,
    val status: String? = null, // "replying", "replied", "error"
    val replyText: String? = null,
    val error: String? = null,

    // Map and Location related state
    val userLocation: Location? = null,
    val distanceToSantaKm: Double? = null,
    val isLocating: Boolean = false,
    val hasLocationPermission: Boolean = false
)