package tech.svehla.gratitudejournal.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import tech.svehla.gratitudejournal.data.local.JournalDao
import tech.svehla.gratitudejournal.data.remote.ApiService
import tech.svehla.gratitudejournal.data.repository.GeneralErrorHandlerImpl
import tech.svehla.gratitudejournal.data.repository.MainRepositoryImpl
import tech.svehla.gratitudejournal.domain.repository.ErrorHandler
import tech.svehla.gratitudejournal.domain.repository.MainRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    @Singleton
    fun provideMainRepository(
        journalDao: JournalDao,
        apiService: ApiService,
        errorHandler: ErrorHandler
    ): MainRepository {
        return MainRepositoryImpl(journalDao, apiService, errorHandler)
    }

    @Provides
    @Singleton
    fun provideErrorHandler(): ErrorHandler {
        return GeneralErrorHandlerImpl()
    }
}
