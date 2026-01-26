package fi.antero.satumaa.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "letters")
data class LetterEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val childName: String,
    val letterText: String,
    val replyText: String?, // Voi olla null, jos vastausta ei ole vielä
    val status: String,     // "replying", "replied", "error"
    val createdAt: Long,
    val repliedAt: Long?    // Voi olla null
)