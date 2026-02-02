package fi.antero.satumaa.data.mapper

import com.google.firebase.Timestamp
import fi.antero.satumaa.data.local.entity.LetterEntity
import fi.antero.satumaa.data.model.Letter
import fi.antero.satumaa.data.remote.dto.LetterDto

// UUSI: Muuntaa pelkän Entityn Domain-malliksi. isOpened annetaan erikseen.
fun LetterEntity.toDomainModel(isOpened: Boolean = false): Letter {
    return Letter(
        id = id,
        userId = userId,
        letterText = letterText,
        status = status,

        createdAt = Timestamp(createdAt / 1000, ((createdAt % 1000) * 1000000).toInt()),
        replyText = replyText,
        repliedAt = repliedAt?.let { Timestamp(it / 1000, ((it % 1000) * 1000000).toInt()) },
        isOpened = isOpened
    )
}

// Muuntaa DTO:n (pilvestä) Entityksi (tietokantaan)
// Huom: LetterEntity ei enää sisällä isOpened-kenttää
fun LetterDto.toEntity(): LetterEntity {
    return LetterEntity(
        id = id,
        userId = userId,
        childName = childName,
        letterText = letterText,
        replyText = replyText,
        status = status,
        createdAt = createdAt,
        repliedAt = repliedAt
    )
}