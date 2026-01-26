package fi.antero.satumaa.viewmodel.letter

import android.location.Location

data class LetterUiState(
    // Mitä käyttäjä kirjoittaa tekstikenttään parhaillaan
    val text: String = "",

    // Käyttäjän lähettämä teksti (tallennetaan, jotta se näkyy vastauksen yläpuolella)
    val sentText: String = "",

    // True kun lähetystä ollaan käynnistämässä / odotetaan repositoryn vastausta
    val isSending: Boolean = false,

    // Kirjeen "tila": null, "replying", "replied" tai "error"
    val status: String? = null,

    // Pukin vastausteksti (kun status == "replied")
    val replyText: String? = null,

    // Virheviesti UI:lle (näytetään ErrorView:ssä)
    val error: String? = null,

    // True jos käytettiin offline-demoa (eli backend ei ollut käytössä)
    val usedOfflineDemo: Boolean = false,

    // Kertoo, ollaanko katsomassa vanhaa kirjettä (estää automaattisen nollauksen)
    val isViewMode: Boolean = false,

    // Mahdollinen ilmoitus uuden vastauksen saapumisesta
    val showReplyArrived: Boolean = false,

    // Karttaan ja sijaintiin liittyvät tilat
    val userLocation: Location? = null,
    val distanceToSantaKm: Double? = null,
    val isLocating: Boolean = false,
    val hasLocationPermission: Boolean = false
)