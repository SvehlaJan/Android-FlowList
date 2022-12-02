package tech.svehla.gratitudejournal.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import tech.svehla.gratitudejournal.data.remote.ApiService
import tech.svehla.gratitudejournal.data.remote.AuthService
import tech.svehla.gratitudejournal.data.remote.implementation.AuthServiceImpl
import tech.svehla.gratitudejournal.data.remote.implementation.FirestoreServiceImpl
import tech.svehla.gratitudejournal.data.remote.util.RequestInterceptor
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(@ApplicationContext context: Context): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(RequestInterceptor())
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(
                "https://gist.githubusercontent.com/skydoves/176c209dbce4a53c0ff9589e07255f30/raw/6489d9712702e093c4df71500fb822f0d408ef75/"
            )
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

//  @Provides
//  @Singleton
//  fun provideApiService(retrofit: Retrofit): ApiService {
//    return retrofit.create(ApiService::class.java)
//  }

    @Provides
    @Singleton
    fun provideFirestoreService(authService: AuthService): ApiService {
        return FirestoreServiceImpl(authService)
    }

    @Provides
    @Singleton
    fun provideAuthService(): AuthService {
        return AuthServiceImpl()
    }
}
