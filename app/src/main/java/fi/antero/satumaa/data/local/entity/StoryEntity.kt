package fi.antero.satumaa.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Tietokantataulu saduille ('stories').
 *
 * Tallentaa käyttäjän luomat sadut pysyvästi laitteelle.
 * Tämä toimii "Kirjahyllynä", josta satuja voi lukea myös ilman verkkoyhteyttä.
 */
@Entity(tableName = "stories")
data class StoryEntity(
    @PrimaryKey
    val id: String,             // Yksilöllinen tunniste
    val title: String,          // Sadun otsikko
    val content: String,        // Varsinainen sadun teksti
    val childName: String,      // Kenelle satu luotiin
    val style: String,          // Sadun tyyli (esim. "EXCITING", "FUNNY")
    val keywords: String,       // Taikasanat tallennettuna merkkijonona (esim. "miekka,linna,lohikäärme")
    val createdAt: Long,        // Luontiaika (aikaleima)
    val isFavorite: Boolean = false // Mahdollisuus merkitä suosikiksi (tulevaisuuden ominaisuus)
)