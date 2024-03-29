package tech.svehla.gratitudejournal.core.di

import android.app.Application
import androidx.room.Room
import tech.svehla.gratitudejournal.core.data.local.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import tech.svehla.gratitudejournal.R
import tech.svehla.gratitudejournal.core.data.local.JournalDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PersistenceModule {

  @Provides
  @Singleton
  fun provideAppDatabase(application: Application): AppDatabase {
    return Room
      .databaseBuilder(
        application,
        AppDatabase::class.java,
        application.getString(R.string.database)
      )
      .fallbackToDestructiveMigration()
      .build()
  }

  @Provides
  @Singleton
  fun providePosterDao(appDatabase: AppDatabase): JournalDao {
    return appDatabase.journalDao()
  }
}
