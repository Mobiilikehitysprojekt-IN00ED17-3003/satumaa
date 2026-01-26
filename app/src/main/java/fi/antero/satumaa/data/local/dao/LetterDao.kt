package fi.antero.satumaa.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import fi.antero.satumaa.data.local.entity.LetterEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LetterDao {
    // Hakee käyttäjän kirjeet aikajärjestyksessä (uusin ensin)
    @Query("SELECT * FROM letters WHERE userId = :userId ORDER BY createdAt DESC")
    fun getLettersByUserId(userId: String): Flow<List<LetterEntity>>

    // Hakee yksittäisen kirjeen ID:n perusteella
    @Query("SELECT * FROM letters WHERE id = :id")
    suspend fun getLetterById(id: String): LetterEntity?

    // Tallentaa tai päivittää yhden kirjeen
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLetter(letter: LetterEntity)

    // Tallentaa tai päivittää listan kirjeitä (synkronointia varten)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLetters(letters: List<LetterEntity>)

    // Poistaa yksittäisen kirjeen
    @Query("DELETE FROM letters WHERE id = :id")
    suspend fun deleteLetter(id: String)

    // Poistaa kaikki tietyn käyttäjän kirjeet (esim. uloskirjautuessa)
    @Query("DELETE FROM letters WHERE userId = :userId")
    suspend fun deleteAllUserLetters(userId: String)
}