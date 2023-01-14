package tech.svehla.gratitudejournal.core.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import tech.svehla.gratitudejournal.core.data.local.JournalDao
import tech.svehla.gratitudejournal.core.data.remote.ApiService
import tech.svehla.gratitudejournal.core.data.repository.GeneralErrorHandlerImpl
import tech.svehla.gratitudejournal.core.data.repository.MainRepositoryImpl
import tech.svehla.gratitudejournal.core.domain.repository.ErrorHandler
import tech.svehla.gratitudejournal.core.domain.repository.MainRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    @Singleton
    fun provideMainRepository(
        journalDao: JournalDao,
        apiService: ApiService,
    ): MainRepository {
        return MainRepositoryImpl(journalDao, apiService)
    }

    @Provides
    @Singleton
    fun provideErrorHandler(): ErrorHandler {
        return GeneralErrorHandlerImpl()
    }
}
