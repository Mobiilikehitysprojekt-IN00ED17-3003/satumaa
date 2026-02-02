package fi.antero.satumaa.data.mapper

import fi.antero.satumaa.data.local.entity.StoryEntity
import fi.antero.satumaa.data.model.Story
import fi.antero.satumaa.data.remote.dto.StoryDto

// Tietokannasta -> Sovellukseen
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
        previewId = null
    )
}

// Sovelluksesta -> Tietokantaan (Entity)
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
        // HUOM: Emme tallenna previewId:tä paikalliseen kantaan, joten sitä ei tässä tarvita
    )
}

// Pilvestä (DTO) -> Tietokantaan (Entity)
// Tätä käytetään vain vanhojen satujen latauksessa listaan
fun StoryDto.toEntity(): StoryEntity {
    return StoryEntity(
        id = id,
        title = title,
        content = content,
        childName = childName,
        style = style,
        keywords = keywords.joinToString(", "),
        createdAt = createdAt,
        isFavorite = isFavorite
    )
}