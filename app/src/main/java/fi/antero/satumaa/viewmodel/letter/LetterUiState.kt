package fi.antero.satumaa.viewmodel.letter

import android.location.Location

data class LetterUiState(
    // Kirjeeseen liittyvät tilat
    val text: String = "",
    val isSending: Boolean = false,
    val status: String? = null,
    val replyText: String? = null,
    val error: String? = null,
    val usedOfflineDemo: Boolean = false,


    val userLocation: Location? = null,
    val distanceToSantaKm: Double? = null,
    val isLocating: Boolean = false,
    val hasLocationPermission: Boolean = false
)