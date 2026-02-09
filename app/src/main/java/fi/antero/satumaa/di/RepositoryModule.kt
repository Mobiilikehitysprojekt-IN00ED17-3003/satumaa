package fi.antero.satumaa.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import fi.antero.satumaa.data.repository.AuthRepository
import fi.antero.satumaa.data.repository.AuthRepositoryImpl
import fi.antero.satumaa.data.repository.LetterRepository
import fi.antero.satumaa.data.repository.LetterRepositoryImpl
import fi.antero.satumaa.data.repository.LocationRepository
import fi.antero.satumaa.data.repository.LocationRepositoryImpl
import fi.antero.satumaa.data.repository.StoryRepository
import fi.antero.satumaa.data.repository.StoryRepositoryImpl
import javax.inject.Singleton

/**
 * Hilt-moduuli, joka sitoo rajapinnat (Interfaces) niiden toteutuksiin (Implementations).
 *
 * Käytämme 'abstract class' ja '@Binds' -anotaatiota, koska se on tehokkaampi tapa
 * kertoa Hiltille rajapintojen toteutukset kuin '@Provides'.
 * Hilt generoi tarvittavan koodin taustalla.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    /**
     * Kun joku pyytää [AuthRepository]-rajapintaa,
     * Hilt injektoi [AuthRepositoryImpl]-instanssin.
     */
    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    /**
     * Kun joku pyytää [LetterRepository]-rajapintaa,
     * Hilt injektoi [LetterRepositoryImpl]-instanssin.
     */
    @Binds
    @Singleton
    abstract fun bindLetterRepository(
        letterRepositoryImpl: LetterRepositoryImpl
    ): LetterRepository

    /**
     * Kun joku pyytää [StoryRepository]-rajapintaa,
     * Hilt injektoi [StoryRepositoryImpl]-instanssin.
     */
    @Binds
    @Singleton
    abstract fun bindStoryRepository(
        storyRepositoryImpl: StoryRepositoryImpl
    ): StoryRepository

    /**
     * Kun joku pyytää [LocationRepository]-rajapintaa,
     * Hilt injektoi [LocationRepositoryImpl]-instanssin.
     */
    @Binds
    @Singleton
    abstract fun bindLocationRepository(
        locationRepositoryImpl: LocationRepositoryImpl
    ): LocationRepository
}