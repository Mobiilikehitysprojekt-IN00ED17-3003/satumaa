package fi.antero.satumaa.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import fi.antero.satumaa.data.local.entity.StoryEntity
import kotlinx.coroutines.flow.Flow

/**
 * StoryDao (Data Access Object) määrittelee tietokantaoperaatiot saduille.
 *
 * Tämä rajapinta vastaa 'stories'-taulun käsittelystä, johon tallennetaan
 * käyttäjän luomat ja tallentamat sadut.
 */
@Dao
interface StoryDao {

    // --- HAKUOPERAATIOT (READ) ---

    /**
     * Hakee kaikki sadut tietokannasta luontijärjestyksessä (uusin ensin).
     * Palauttaa Flow'n, joka emittoi uuden listan automaattisesti aina,
     * kun taulun sisältö muuttuu (esim. uusi satu lisätään tai poistetaan).
     *
     * @return Flow, joka sisältää listan StoryEntity-objekteja.
     */
    @Query("SELECT * FROM stories ORDER BY createdAt DESC")
    fun getAllStories(): Flow<List<StoryEntity>>

    /**
     * Hakee yksittäisen sadun sen ID:n perusteella.
     * Käytetään esimerkiksi sadun yksityiskohtien näyttämiseen tai
     * tarkistamaan, onko satu jo olemassa ennen tallennusta.
     *
     * @param storyId Sadun yksilöllinen tunniste.
     * @return StoryEntity tai null, jos satua ei löydy.
     */
    @Query("SELECT * FROM stories WHERE id = :storyId")
    suspend fun getStoryById(storyId: String): StoryEntity?

    // --- KIRJOITUSOPERAATIOT (CREATE / UPDATE) ---

    /**
     * Tallentaa uuden sadun tai päivittää olemassa olevan.
     * OnConflictStrategy.REPLACE varmistaa, että jos ID on jo käytössä,
     * vanha rivi korvataan uudella (päivitys).
     *
     * @param story Tallennettava StoryEntity.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStory(story: StoryEntity)

    /**
     * Tallentaa listan satuja kerralla.
     * Käytetään tehokkuussyistä synkronoinnissa, kun haetaan useita satuja pilvestä.
     *
     * @param stories Lista tallennettavia StoryEntity-objekteja.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStories(stories: List<StoryEntity>)

    // --- POISTO-OPERAATIOT (DELETE) ---

    /**
     * Poistaa yksittäisen sadun tietokannasta ID:n perusteella.
     *
     * @param storyId Poistettavan sadun tunniste.
     */
    @Query("DELETE FROM stories WHERE id = :storyId")
    suspend fun deleteStory(storyId: String)
}