package fi.antero.satumaa.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import fi.antero.satumaa.data.local.dao.StoryDao
import fi.antero.satumaa.data.local.entity.StoryEntity

// Määritellään, mitkä Entityt kuuluvat tähän tietokantaan
@Database(entities = [StoryEntity::class], version = 1, exportSchema = false)
abstract class SatumaaDatabase : RoomDatabase() {
    // Kerrotaan Roomille, että tästä kannasta saa ulos StoryDaon
    abstract fun storyDao(): StoryDao
}