package fi.antero.satumaa.data.mapper

import fi.antero.satumaa.data.local.entity.StoryEntity
import fi.antero.satumaa.data.model.Story
import fi.antero.satumaa.data.remote.dto.StoryDto

// Muuntaa tietokantarivin (Entity) sovelluksen käyttämäksi malliksi (Story)
fun StoryEntity.toDomainModel(): Story {
    return Story(
        id = id,
        title = title,
        content = content,
        childName = childName,
        style = style,
        keywords = keywords,
        createdAt = createdAt,
        isFavorite = isFavorite
    )
}

// Jos tarvitsemme toisinpäin (esim. jos muokkaamme satua UI:ssa ja tallennamme):
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
    )
}

//  Muuntaa pilvidatan (DTO) tietokantariviksi (Entity)
fun StoryDto.toEntity(): StoryEntity {
    return StoryEntity(
        id = id,
        title = title,
        content = content,
        childName = childName,
        style = style,
        // Muunnetaan lista takaisin yhdeksi merkkijonoksi tietokantaa varten
        keywords = keywords.joinToString(", "),
        createdAt = createdAt,
        isFavorite = isFavorite
    )
}