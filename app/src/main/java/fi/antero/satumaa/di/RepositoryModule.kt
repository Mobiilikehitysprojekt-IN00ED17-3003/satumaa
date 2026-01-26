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
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindLetterRepository(
        letterRepositoryImpl: LetterRepositoryImpl
    ): LetterRepository

    @Binds
    @Singleton
    abstract fun bindLocationRepository(
        locationRepositoryImpl: LocationRepositoryImpl
    ): LocationRepository
}