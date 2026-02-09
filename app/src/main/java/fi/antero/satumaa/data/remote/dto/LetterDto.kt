package fi.antero.satumaa.data.remote.dto

import com.google.firebase.firestore.DocumentSnapshot

/**
 * LetterDto (Data Transfer Object) on välitysobjekti Firebasen ja sovelluksen välillä.
 *
 * Sen tehtävä on:
 * 1. Määritellä odotettu tietorakenne pilvestä.
 * 2. Toimia välikätenä ennen datan tallennusta paikalliseen tietokantaan (Entity).
 *
 * DTO:t ovat kevyitä luokkia, joissa ei ole logiikkaa.
 */
data class LetterDto(
    val id: String,
    val userId: String,
    val childName: String,     // Lapsen nimi, jos tallennettu
    val letterText: String,    // Lähetetty viesti
    val replyText: String?,    // Pukin vastaus (voi olla null)
    val status: String,        // Tila (esim. "replied")
    val createdAt: Long,       // Luontiaika (millisekunteina)
    val repliedAt: Long?       // Vastausaika (millisekunteina)
)

/**
 * Laajennusfunktio, joka muuntaa Firestoren raakadatan (DocumentSnapshot) turvalliseksi DTO:ksi.
 *
 * Ominaisuudet:
 * - Null-turvallisuus: Jos pakolliset kentät (userId, letterText) puuttuvat, palauttaa null.
 * - Aikaleimojen käsittely: Muuntaa Firestoren Timestamp-objektit Long-millisekuneiksi.
 * - Oletusarvot: Jos vapaaehtoisia kenttiä puuttuu, käytetään järkeviä oletuksia.
 */
fun DocumentSnapshot.toLetterDto(): LetterDto? {
    // Haetaan kriittiset kentät
    val userId = getString("userId")
    val letterText = getString("letterText")

    // Validointi: Jos nämä puuttuvat, dokumentti on rikki -> hylätään
    if (userId == null || letterText == null) return null

    // Haetaan aikaleimat
    val createdTs = getTimestamp("createdAt")
    val repliedTs = getTimestamp("repliedAt")

    return LetterDto(
        id = id, // Dokumentin ID Firebasesta
        userId = userId,
        childName = getString("childName") ?: "", // Oletus: tyhjä merkkijono
        letterText = letterText,
        replyText = getString("replyText"),
        status = getString("status") ?: "replying", // Oletus: "vastaamassa"

        // Aikaleimojen muunnos (Timestamp -> Date -> Long)
        // Jos luontiaika puuttuu, käytetään nykyhetkeä (fallback)
        createdAt = createdTs?.toDate()?.time ?: System.currentTimeMillis(),
        repliedAt = repliedTs?.toDate()?.time
    )
}