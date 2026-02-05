package fi.antero.satumaa.viewmodel.letter

import android.location.Location
import fi.antero.satumaa.util.MathChallenge

data class LetterUiState(
    val currentLetterId: String? = null,
    val currentLetterCreatedAtMs: Long? = null,

    val text: String = "",
    val sentText: String = "",

    val isSending: Boolean = false,

    val status: String? = null,
    val replyText: String? = null,

    val error: String? = null,

    val usedOfflineDemo: Boolean = false,

    val isViewMode: Boolean = false,

    val showReplyArrived: Boolean = false,

    val isOpened: Boolean = false,

    val isNewLetterMode: Boolean = false,

    val userLocation: Location? = null,
    val isLocating: Boolean = false,
    val hasLocationPermission: Boolean = false,
    val isLocationEnabled: Boolean = false,
    val locationError: String? = null,

    val isMathDialogVisible: Boolean = false,
    val mathChallenge: MathChallenge? = null,
    val mathError: Boolean = false
)
