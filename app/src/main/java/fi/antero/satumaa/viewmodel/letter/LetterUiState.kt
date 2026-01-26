package fi.antero.satumaa.viewmodel.letter

import android.location.Location

data class LetterUiState(
    val text: String = "",
    val sentText: String = "",
    val isSending: Boolean = false,
    val status: String? = null,
    val replyText: String? = null,
    val error: String? = null,
    val usedOfflineDemo: Boolean = false,

    // Kertoo, ollaanko katsomassa vanhaa kirjettä
    val isViewMode: Boolean = false,

    // Kartta
    val userLocation: Location? = null,
    val distanceToSantaKm: Double? = null,
    val isLocating: Boolean = false,
    val hasLocationPermission: Boolean = false
)