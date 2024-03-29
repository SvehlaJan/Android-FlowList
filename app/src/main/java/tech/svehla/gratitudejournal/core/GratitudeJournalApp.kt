package tech.svehla.gratitudejournal.core

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import tech.svehla.gratitudejournal.BuildConfig
import tech.svehla.gratitudejournal.core.util.CrashReportingTree
import timber.log.Timber.DebugTree
import timber.log.Timber.Forest.plant

@HiltAndroidApp
class GratitudeJournalApp : Application() {
    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            plant(DebugTree())
        } else {
            plant(CrashReportingTree())
        }
    }
}