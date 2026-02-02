package fi.antero.satumaa.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import fi.antero.satumaa.data.local.entity.LetterEntity
import fi.antero.satumaa.data.local.entity.LetterLocalStateEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LetterDao {
    // Hakee pelkät kirjeet (cloud data)
    @Query("SELECT * FROM letters WHERE userId = :userId ORDER BY createdAt DESC")
    fun getLettersOnly(userId: String): Flow<List<LetterEntity>>

    // Hakee kaikkien kirjeiden paikalliset tilat
    @Query("SELECT * FROM letter_local_state")
    fun getAllLocalStates(): Flow<List<LetterLocalStateEntity>>

    // Hakee yksittäisen kirjeen entityn
    @Query("SELECT * FROM letters WHERE id = :id")
    suspend fun getLetterEntityById(id: String): LetterEntity?

    // Hakee yksittäisen kirjeen tilan
    @Query("SELECT * FROM letter_local_state WHERE letterId = :id")
    suspend fun getLocalStateById(id: String): LetterLocalStateEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLetter(letter: LetterEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLetters(letters: List<LetterEntity>)

    @Query("DELETE FROM letters WHERE id = :id")
    suspend fun deleteLetter(id: String)

    @Query("DELETE FROM letters WHERE userId = :userId")
    suspend fun deleteAllUserLetters(userId: String)

    @Query("INSERT OR REPLACE INTO letter_local_state (letterId, isOpened) VALUES (:id, 1)")
    suspend fun markAsOpened(id: String)
}