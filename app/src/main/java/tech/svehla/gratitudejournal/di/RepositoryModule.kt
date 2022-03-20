package tech.svehla.gratitudejournal.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import tech.svehla.gratitudejournal.data.remote.ApiService
import tech.svehla.gratitudejournal.data.remote.FirestoreService
import tech.svehla.gratitudejournal.data.local.JournalDao
import tech.svehla.gratitudejournal.data.repository.MainRepository

@Module
@InstallIn(ViewModelComponent::class)
object RepositoryModule {

  @Provides
  @ViewModelScoped
  fun provideMainRepository(
    apiService: ApiService,
    journalDao: JournalDao,
    firestoreService: FirestoreService
  ): MainRepository {
    return MainRepository(apiService, journalDao, firestoreService)
  }
}
