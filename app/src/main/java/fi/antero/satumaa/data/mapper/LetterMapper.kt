package fi.antero.satumaa.data.mapper

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import fi.antero.satumaa.data.local.entity.LetterEntity
import fi.antero.satumaa.data.model.Letter
import fi.antero.satumaa.data.remote.dto.LetterDto
import fi.antero.satumaa.data.remote.dto.toLetterDto

fun LetterEntity.toDomainModel(): Letter {
    return Letter(
        id = id,
        userId = userId,
        letterText = letterText,
        status = status,
        createdAt = Timestamp(createdAt / 1000, ((createdAt % 1000) * 1000000).toInt()),
        replyText = replyText,
        repliedAt = repliedAt?.let { Timestamp(it / 1000, ((it % 1000) * 1000000).toInt()) }
    )
}

// Muuntaa DTO:n (pilvestä) Entityksi (tietokantaan)
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
