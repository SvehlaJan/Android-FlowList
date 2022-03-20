package tech.svehla.gratitudejournal

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import tech.svehla.gratitudejournal.common.util.CrashReportingTree
import timber.log.Timber.*
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