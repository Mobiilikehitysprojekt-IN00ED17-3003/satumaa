package fi.antero.satumaa.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Tietokantataulu kirjeiden paikalliselle tilalle ('letter_local_state').
 *
 * Tämä taulu on erotettu 'letters'-taulusta arkkitehtuurisista syistä:
 * 1. **Data vs. UI-tila:** 'letters' sisältää sisällön, tämä sisältää käyttökokemuksen tilan.
 * 2. **Synkronointi:** Kun sovellus hakee tuoreet kirjeet pilvestä, 'letters'-taulu
 * voidaan ylikirjoittaa huoletta ilman, että tieto siitä "onko kirje jo avattu" katoaa.
 */
@Entity(tableName = "letter_local_state")
data class LetterLocalStateEntity(
    @PrimaryKey val letterId: String, // Viiteavain LetterEntityyn
    val isOpened: Boolean = false     // Onko käyttäjä avannut/nähnyt vastauksen animaation?
)