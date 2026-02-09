package fi.antero.satumaa.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import fi.antero.satumaa.data.local.dao.LetterDao
import fi.antero.satumaa.data.local.dao.StoryDao
import fi.antero.satumaa.data.local.entity.LetterEntity
import fi.antero.satumaa.data.local.entity.LetterLocalStateEntity
import fi.antero.satumaa.data.local.entity.StoryEntity

/**
 * Sovelluksen paikallinen Room-tietokanta.
 *
 * Määrittelee:
 * 1. Tietokannan taulut (entities).
 * 2. Tietokannan version (version = 4).
 * 3. Pääsyn DAO-rajapintoihin (Data Access Objects).
 *
 * exportSchema = false: Estää skeeman viennin JSON-tiedostoon käännöksen aikana.
 * (Oikeissa tuotantoprojekteissa tämä on usein true migraatioiden testaamista varten).
 */
@Database(
    entities = [
        StoryEntity::class,
        LetterEntity::class,
        LetterLocalStateEntity::class
    ],
    version = 4,
    exportSchema = false
)
abstract class SatumaaDatabase : RoomDatabase() {

    // DAO:t tietokantakyselyiden suorittamiseen
    abstract fun storyDao(): StoryDao
    abstract fun letterDao(): LetterDao
}