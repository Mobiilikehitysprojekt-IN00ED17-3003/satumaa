package fi.antero.satumaa.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import fi.antero.satumaa.data.local.SatumaaDatabase
import fi.antero.satumaa.data.local.dao.LetterDao
import fi.antero.satumaa.data.local.dao.StoryDao
import javax.inject.Singleton

/**
 * Hilt-moduuli tietokantariippuvuuksille.
 *
 * @InstallIn(SingletonComponent::class) tarkoittaa, että tässä määritellyt riippuvuudet
 * elävät koko sovelluksen elinkaaren ajan (Singleton).
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    /**
     * Luo ja palauttaa varsinaisen Room-tietokannan instanssin.
     *
     * @param context Application Context (tarvitaan tietokannan luontiin).
     * @return SatumaaDatabase-instanssi.
     */
    @Provides
    @Singleton // Varmistaa, että tietokanta luodaan vain kerran (kallis operaatio).
    fun provideSatumaaDatabase(
        @ApplicationContext context: Context
    ): SatumaaDatabase {
        return Room.databaseBuilder(
            context,
            SatumaaDatabase::class.java,
            "satumaa_database"
        )
            // Kehitysvaiheessa hyödyllinen: jos tietokantarakenne muuttuu,
            // eikä migraatiota ole tehty, tuhoa vanha kanta ja luo uusi.
            // (Tuotannossa tämä poistaisi käyttäjän datan päivityksen yhteydessä!)
            .fallbackToDestructiveMigration()
            .build()
    }

    /**
     * Tarjoaa StoryDao:n riippuvuuden.
     * Hilt osaa hakea tämän, koska 'provideSatumaaDatabase' on määritelty yllä.
     */
    @Provides
    @Singleton
    fun provideStoryDao(database: SatumaaDatabase): StoryDao {
        return database.storyDao()
    }

    /**
     * Tarjoaa LetterDao:n riippuvuuden.
     */
    @Provides
    @Singleton
    fun provideLetterDao(database: SatumaaDatabase): LetterDao {
        return database.letterDao()
    }
}