package fi.antero.satumaa.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import fi.antero.satumaa.data.local.dao.LetterDao
import fi.antero.satumaa.data.local.dao.StoryDao
import fi.antero.satumaa.data.local.entity.LetterEntity
import fi.antero.satumaa.data.local.entity.LetterLocalStateEntity
import fi.antero.satumaa.data.local.entity.StoryEntity

@Database(
    entities = [StoryEntity::class, LetterEntity::class, LetterLocalStateEntity::class],
    version = 4,
    exportSchema = false
)
abstract class SatumaaDatabase : RoomDatabase() {
    abstract fun storyDao(): StoryDao
    abstract fun letterDao(): LetterDao
}