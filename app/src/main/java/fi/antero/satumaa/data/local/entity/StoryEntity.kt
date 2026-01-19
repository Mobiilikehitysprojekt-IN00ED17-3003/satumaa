package fi.antero.satumaa.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stories")
data class StoryEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val content: String,
    val childName: String,
    val style: String,
    val keywords: String,
    val createdAt: Long,
    val isFavorite: Boolean = false
)