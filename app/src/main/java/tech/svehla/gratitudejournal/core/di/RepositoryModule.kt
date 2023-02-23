package tech.svehla.gratitudejournal.core.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import tech.svehla.gratitudejournal.core.data.local.JournalDao
import tech.svehla.gratitudejournal.core.data.remote.ApiService
import tech.svehla.gratitudejournal.core.data.remote.util.ErrorHandlerImpl
import tech.svehla.gratitudejournal.core.data.repository.MainRepositoryImpl
import tech.svehla.gratitudejournal.core.domain.repository.MainRepository
import tech.svehla.gratitudejournal.core.domain.util.ErrorHandler
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideErrorHandler(): ErrorHandler {
        return ErrorHandlerImpl()
    }

    @Provides
    @Singleton
    fun provideMainRepository(
        journalDao: JournalDao,
        apiService: ApiService,
        errorHandler: ErrorHandler,
    ): MainRepository {
        return MainRepositoryImpl(journalDao, apiService, errorHandler)
    }
}
