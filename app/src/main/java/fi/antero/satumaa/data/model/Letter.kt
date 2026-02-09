package fi.antero.satumaa.data.model

import com.google.firebase.Timestamp

/**
 * Domain-malli kirjeelle ('Letter').
 *
 * Tämä luokka edustaa sovelluksen sisäistä käsitystä kirjeestä.
 * Se yhdistää tiedot, jotka tulevat pilvestä (teksti, vastaus, aikaleimat)
 * ja tiedot, jotka ovat laitekohtaisia (kuten 'isOpened').
 *
 * @param id Kirjeen yksilöllinen tunniste (Firestore Document ID).
 * @param userId Käyttäjän ID, johon kirje liittyy.
 * @param letterText Lapsen kirjoittama viesti Joulupukille.
 * @param status Kirjeen tila:
 * - "replying": Odottaa vastausta (Pukki kirjoittaa...)
 * - "replied": Vastaus on valmis
 * - "error": Jotain meni pieleen generoinnissa
 * @param createdAt Luontiaika (Firestore Timestamp).
 * @param replyText Pukin vastaus (null, jos status on "replying").
 * @param repliedAt Vastauksen saapumisaika (null, jos ei vastattu).
 * @param errorMessage Tekninen virheviesti, jos status on "error".
 * @param isOpened Tieto siitä, onko käyttäjä nähnyt vastauksen (animaatio katsottu).
 * Tämä tieto tulee paikallisesta Room-tietokannasta (letter_local_state).
 */
data class Letter(
    val id: String = "",
    val userId: String = "",
    val letterText: String = "",
    val status: String = "replying",
    val createdAt: Timestamp? = null,
    val replyText: String? = null,
    val repliedAt: Timestamp? = null,
    val errorMessage: String? = null,
    val isOpened: Boolean = false
)