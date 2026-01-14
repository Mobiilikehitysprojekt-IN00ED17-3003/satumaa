package fi.antero.satumaa.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import fi.antero.satumaa.data.local.entity.StoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StoryDao {
    // Hakee kaikki sadut aikajärjestyksessä (uusin ensin)
    // Palauttaa Flow:n, eli lista päivittyy UI:ssa automaattisesti
    @Query("SELECT * FROM stories ORDER BY createdAt DESC")
    fun getAllStories(): Flow<List<StoryEntity>>

    // Hakee yksittäisen sadun ID:n perusteella
    @Query("SELECT * FROM stories WHERE id = :storyId")
    suspend fun getStoryById(storyId: String): StoryEntity?

    // Tallentaa sadun. Jos ID on jo olemassa, korvaa vanhan (OnConflictStrategy.REPLACE)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStory(story: StoryEntity)
}