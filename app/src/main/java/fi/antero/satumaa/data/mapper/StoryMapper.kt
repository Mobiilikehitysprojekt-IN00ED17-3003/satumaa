package fi.antero.satumaa.data.mapper

import fi.antero.satumaa.data.local.entity.StoryEntity
import fi.antero.satumaa.data.model.Story
import fi.antero.satumaa.data.remote.dto.StoryDto

/**
 * Muuntaa tietokantarivin (Entity) sovelluksen Domain-malliksi (Story).
 *
 * Tätä käytetään, kun sadut ladataan "Kirjahylly"-näkymään.
 * Room tallentaa avainsanat yhtenä merkkijonona, mutta Domain-malli käyttää sitä sellaisenaan
 * (tai UI voi pilkkoa sen tarvittaessa).
 */
fun StoryEntity.toDomainModel(): Story {
    return Story(
        id = id,
        title = title,
        content = content,
        childName = childName,
        style = style,
        keywords = keywords,
        createdAt = createdAt,
        isFavorite = isFavorite,
        // Tietokannasta ladattaessa previewId on aina null, koska satu on jo tallennettu
        previewId = null
    )
}

/**
 * Muuntaa sovelluksen Domain-mallin (Story) tietokantaan tallennettavaksi Entityksi.
 *
 * Tätä käytetään, kun käyttäjä painaa "Tallenna kirjahyllyyn" generoinnin jälkeen.
 */
fun Story.toEntity(): StoryEntity {
    return StoryEntity(
        id = id,
        title = title,
        content = content,
        childName = childName,
        style = style,
        keywords = keywords,
        createdAt = createdAt,
        isFavorite = isFavorite
        // HUOM: Emme tallenna previewId:tä paikalliseen kantaan, koska se on vain
        // väliaikainen tunniste generointivaiheessa.
    )
}

/**
 * Muuntaa pilvestä (DTO) saapuvan sadun paikalliseen tietokantaan (Entity).
 *
 * Käytetään synkronoinnissa (varmuuskopioiden palautus pilvestä).
 *
 * Erityistä:
 * - DTO:ssa avainsanat saattavat olla List<String> (riippuen backendistä),
 * mutta Entityssä ne ovat yksi String. Tässä tehdään 'joinToString' tarvittaessa,
 * tai jos DTO on jo litistetty, kopioidaan suoraan.
 */
fun StoryDto.toEntity(): StoryEntity {
    return StoryEntity(
        id = id,
        title = title,
        content = content,
        childName = childName,
        style = style,
        // DTO:n keywords on lista -> muutetaan pilkulla erotetuksi merkkijonoksi tietokantaa varten
        keywords = keywords.joinToString(", "),
        createdAt = createdAt,
        isFavorite = isFavorite
    )
}