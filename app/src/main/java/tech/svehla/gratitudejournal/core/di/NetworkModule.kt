package tech.svehla.gratitudejournal.core.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import tech.svehla.gratitudejournal.core.data.remote.ApiService
import tech.svehla.gratitudejournal.core.data.remote.AuthService
import tech.svehla.gratitudejournal.core.data.remote.implementation.AuthServiceImpl
import tech.svehla.gratitudejournal.core.data.remote.implementation.FirestoreServiceImpl
import tech.svehla.gratitudejournal.core.data.remote.implementation.KtorApiServiceImpl
import tech.svehla.gratitudejournal.core.data.remote.util.ktorHttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideHttpClient(): HttpClient {
        return ktorHttpClient
    }

    @Provides
    @Singleton
    fun provideApiService(authService: AuthService, client: HttpClient): ApiService {
//        return KtorApiServiceImpl(authService, client)
        return FirestoreServiceImpl(authService)
    }

    @Provides
    @Singleton
    fun provideAuthService(): AuthService {
        return AuthServiceImpl()
    }
}
