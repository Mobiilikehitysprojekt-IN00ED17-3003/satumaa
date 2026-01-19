package fi.antero.satumaa.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import fi.antero.satumaa.data.local.SatumaaDatabase
import fi.antero.satumaa.data.local.dao.StoryDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideSatumaaDatabase(
        @ApplicationContext context: Context
    ): SatumaaDatabase {
        return Room.databaseBuilder(
            context,
            SatumaaDatabase::class.java,
            "satumaa_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideStoryDao(database: SatumaaDatabase): StoryDao {
        return database.storyDao()
    }
}