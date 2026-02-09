package fi.antero.satumaa.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import fi.antero.satumaa.data.local.entity.LetterEntity
import fi.antero.satumaa.data.local.entity.LetterLocalStateEntity
import kotlinx.coroutines.flow.Flow

/**
 * LetterDao (Data Access Object) määrittelee kaikki tietokantaoperaatiot kirjeille.
 *
 * Tämä rajapinta hallinnoi kahta taulua:
 * 1. 'letters': Pilvestä synkronoitu data (varsinainen sisältö).
 * 2. 'letter_local_state': Laitteekohtainen tila (esim. onko kirje avattu animaatiossa).
 */
@Dao
interface LetterDao {

    // --- HAKUOPERAATIOT (READ) ---

    /**
     * Hakee tietyn käyttäjän kirjeet aikajärjestyksessä (uusin ensin).
     * Palauttaa Flow'n, joka emittoi uuden listan aina kun tietokanta muuttuu.
     * Huom: Tämä hakee vain LetterEntityt, ei yhdistettyä tilaa.
     */
    @Query("SELECT * FROM letters WHERE userId = :userId ORDER BY createdAt DESC")
    fun getLettersOnly(userId: String): Flow<List<LetterEntity>>

    /**
     * Hakee kaikkien kirjeiden paikalliset tilatiedot (esim. isOpened).
     * Repository-taso yhdistää tämän datan varsinaisten kirjeiden kanssa 'combine'-operaattorilla.
     */
    @Query("SELECT * FROM letter_local_state")
    fun getAllLocalStates(): Flow<List<LetterLocalStateEntity>>

    /**
     * Hakee yksittäisen kirjeen datan ID:n perusteella.
     * Käytetään esimerkiksi yksittäisen kirjeen lataamiseen tai tarkistuksiin.
     */
    @Query("SELECT * FROM letters WHERE id = :id")
    suspend fun getLetterEntityById(id: String): LetterEntity?

    /**
     * Hakee yksittäisen kirjeen paikallisen tilan.
     */
    @Query("SELECT * FROM letter_local_state WHERE letterId = :id")
    suspend fun getLocalStateById(id: String): LetterLocalStateEntity?

    // --- KIRJOITUSOPERAATIOT (CREATE / UPDATE) ---

    /**
     * Tallentaa tai päivittää yksittäisen kirjeen.
     * OnConflictStrategy.REPLACE tarkoittaa, että jos ID on jo olemassa, vanha data korvataan uudella.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLetter(letter: LetterEntity)

    /**
     * Tallentaa listan kirjeitä kerralla (tehokas batch-operaatio).
     * Käytetään synkronoinnissa, kun haetaan useita kirjeitä pilvestä.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLetters(letters: List<LetterEntity>)

    /**
     * Merkitsee kirjeen avatuksi paikallisessa tilataulussa.
     * Käytetään INSERT OR REPLACE -syntaksia: jos riviä ei ole, se luodaan.
     * Jos rivi on olemassa, se päivitetään. 'isOpened' asetetaan arvoon 1 (true).
     */
    @Query("INSERT OR REPLACE INTO letter_local_state (letterId, isOpened) VALUES (:id, 1)")
    suspend fun markAsOpened(id: String)

    // --- POISTO-OPERAATIOT (DELETE) ---

    /**
     * Poistaa yksittäisen kirjeen tietokannasta ID:n perusteella.
     */
    @Query("DELETE FROM letters WHERE id = :id")
    suspend fun deleteLetter(id: String)

    /**
     * Poistaa kaikki tietyn käyttäjän kirjeet.
     * Käytetään esimerkiksi uloskirjautuessa tai täydellisessä uudelleensynkronoinnissa (cache clear).
     */
    @Query("DELETE FROM letters WHERE userId = :userId")
    suspend fun deleteAllUserLetters(userId: String)
}