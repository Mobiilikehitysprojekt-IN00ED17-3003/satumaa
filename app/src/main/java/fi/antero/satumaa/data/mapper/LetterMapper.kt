package fi.antero.satumaa.data.mapper

import com.google.firebase.Timestamp
import fi.antero.satumaa.data.local.entity.LetterEntity
import fi.antero.satumaa.data.model.Letter
import fi.antero.satumaa.data.remote.dto.LetterDto

/**
 * Muuntaa paikallisen tietokanta-entiteetin sovelluksen sisäiseksi Domain-malliksi.
 *
 * Tämä funktio on "liima", joka yhdistää:
 * 1. Pysyvän datan (LetterEntity)
 * 2. Paikallisen tilan (isOpened, joka tuodaan parametrina toisesta taulusta)
 *
 * Lisäksi tämä hoitaa aikaleimojen muunnoksen tietokannan Long-arvosta (millisekunnit)
 * Domain-mallin käyttämäksi Firebase Timestamp -objektiksi.
 *
 * @param isOpened Tieto siitä, onko käyttäjä nähnyt vastauksen (tulee letter_local_state -taulusta).
 */
fun LetterEntity.toDomainModel(isOpened: Boolean = false): Letter {
    return Letter(
        id = id,
        userId = userId,
        letterText = letterText,
        status = status,

        // Muunnetaan millisekunnit (Long) -> Firebase Timestamp (Seconds + Nanoseconds)
        // Kaava: millisekunnit / 1000 = sekunnit. Jäännös * 1 000 000 = nanosekunnit.
        createdAt = Timestamp(createdAt / 1000, ((createdAt % 1000) * 1000000).toInt()),

        replyText = replyText,

        // Sama muunnos vastausajalle, jos se on olemassa
        repliedAt = repliedAt?.let {
            Timestamp(it / 1000, ((it % 1000) * 1000000).toInt())
        },

        // Asetetaan yhdistetty tila
        isOpened = isOpened
    )
}

/**
 * Muuntaa pilvestä (DTO) saapuvan datan paikalliseen tietokantaan (Entity) sopivaksi.
 *
 * Käytetään synkronoinnissa (SyncWorker / Repository).
 *
 * Tärkeää:
 * - LetterEntity ei sisällä 'isOpened'-tietoa, koska se on laitekohtainen tila,
 * eikä sitä haluta ylikirjoittaa pilvestä tulevalla datalla.
 * - Aikaleimat säilytetään Long-muodossa (millisekunnit) SQLite-tietokannassa.
 */
fun LetterDto.toEntity(): LetterEntity {
    return LetterEntity(
        id = id,
        userId = userId,
        childName = childName,
        letterText = letterText,
        replyText = replyText,
        status = status,
        createdAt = createdAt, // DTO:ssa ja Entityssä molemmissa Long
        repliedAt = repliedAt
    )
}