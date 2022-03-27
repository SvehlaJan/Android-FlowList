package tech.svehla.gratitudejournal.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import tech.svehla.gratitudejournal.presentation.detail.DetailScreenFormDelegate
import tech.svehla.gratitudejournal.presentation.detail.DetailScreenFormDelegateImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ViewModelModule {
    @Provides
    @Singleton
    fun provideDetailScreenFormDelegate(): DetailScreenFormDelegate {
        return DetailScreenFormDelegateImpl()
    }
}