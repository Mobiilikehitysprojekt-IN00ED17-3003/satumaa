package fi.antero.satumaa.viewmodel.letter

import android.location.Location
import fi.antero.satumaa.util.MathChallenge

/**
 * UI-tila (State) kirjenäkymälle.
 *
 * Tämä luokka sisältää kaiken datan, jota tarvitaan kirjeen kirjoittamiseen,
 * lähettämiseen, vastauksen odottamiseen ja lukemiseen, sekä kartan ja
 * vanhempien lukon (MathChallenge) hallintaan.
 */
data class LetterUiState(
    // --- AKTIIVINEN KIRJE ---
    // Mikä kirje on tällä hetkellä tarkastelussa
    val currentLetterId: String? = null,
    val currentLetterCreatedAtMs: Long? = null,

    // --- TEKSTIKENTÄT ---
    val text: String = "",        // Käyttäjän syöttämä teksti (luonnos)
    val sentText: String = "",    // Lähetetty teksti (näytetään odotustilassa)

    // --- LÄHETYS JA TILA ---
    val isSending: Boolean = false, // Onko verkkopyyntö kesken

    val status: String? = null,   // Kirjeen tila (esim. "replying", "replied")
    val replyText: String? = null, // Joulupukin vastaus

    val error: String? = null,    // Virheviesti käyttäjälle

    val usedOfflineDemo: Boolean = false, // Lippu offline-demo -tilalle

    // --- NÄKYMÄTILAT ---
    val isViewMode: Boolean = false,      // Katselutila (vanha kirje), ei muokkausta
    val showReplyArrived: Boolean = false, // Should we show the "Reply Arrived" animation?
    val isOpened: Boolean = false,        // Onko kirje avattu (sinetti rikottu)
    val isNewLetterMode: Boolean = false, // Ollaanko luomassa täysin uutta kirjettä

    // --- SIJAINTITIEDOT (KARTTAA VARTEN) ---
    val userLocation: Location? = null,
    val isLocating: Boolean = false,
    val hasLocationPermission: Boolean = false,
    val isLocationEnabled: Boolean = false,
    val locationError: String? = null,

    // --- PARENTAL GATE (VANHEMPIEN LUKKO) ---
    val isMathDialogVisible: Boolean = false,
    val mathChallenge: MathChallenge? = null,
    val mathError: Boolean = false
)