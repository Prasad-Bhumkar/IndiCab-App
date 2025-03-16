package com.example.indicab.utils

import android.app.Application
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CrashReporter @Inject constructor(application: Application) {
    init {
        // Initialize crash reporting here
        // Example:
        // FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)
        // Timber.plant(Timber.DebugTree())
    }

    fun logException(throwable: Throwable) {
        // Log exception to crash reporting service
    }

    fun logMessage(message: String) {
        // Log message to crash reporting service
    }
}
