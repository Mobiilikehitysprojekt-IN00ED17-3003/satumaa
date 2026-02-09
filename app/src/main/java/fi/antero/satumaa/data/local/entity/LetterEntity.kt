package fi.antero.satumaa.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Tietokantataulu kirjeille ('letters').
 *
 * Tämä on sovelluksen ensisijainen tietolähde kirjeille (Single Source of Truth).
 * UI lukee kirjeet aina tästä paikallisesta Room-tietokannasta.
 *
 * Tietojen päivitys tapahtuu Repositoryn kautta:
 * 1. Sovellus hakee tuoreimmat tiedot pilvestä (esim. Firestore).
 * 2. Pilvestä saatu data tallennetaan tähän tauluun.
 * 3. UI reagoi tämän taulun muutoksiin automaattisesti (Flow).
 */
@Entity(tableName = "letters")
data class LetterEntity(
    @PrimaryKey
    val id: String,             // Yksilöllinen tunniste (sama kuin pilvessä)
    val userId: String,         // Käyttäjän ID, johon kirje kuuluu
    val childName: String,      // Lapsen nimi
    val letterText: String,     // Lähetetty viesti
    val replyText: String?,     // Pukin vastaus (null, jos ei vielä vastattu)
    val status: String,         // Tila: "replying" (odottaa), "replied" (valmis), "error" (virhe)
    val createdAt: Long,        // Luontiaika (aikaleima)
    val repliedAt: Long?        // Vastauksen saapumisaika (null, jos ei vastattu)
)