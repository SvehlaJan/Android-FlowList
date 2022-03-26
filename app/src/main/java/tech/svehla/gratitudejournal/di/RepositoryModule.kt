package tech.svehla.gratitudejournal.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import tech.svehla.gratitudejournal.data.remote.ApiService
import tech.svehla.gratitudejournal.data.local.JournalDao
import tech.svehla.gratitudejournal.data.repository.MainRepositoryImpl
import tech.svehla.gratitudejournal.domain.repository.MainRepository

@Module
@InstallIn(ViewModelComponent::class)
object RepositoryModule {

  @Provides
  @ViewModelScoped
  fun provideMainRepository(
    journalDao: JournalDao,
    apiService: ApiService
  ): MainRepository {
    return MainRepositoryImpl(journalDao, apiService)
  }
}
