package fi.antero.satumaa.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "letter_local_state")
data class LetterLocalStateEntity(
    @PrimaryKey val letterId: String,
    val isOpened: Boolean = false
)