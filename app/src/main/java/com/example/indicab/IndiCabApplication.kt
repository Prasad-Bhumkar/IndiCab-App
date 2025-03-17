package com.example.indicab

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class IndiCabApplication : Application() {

    @Inject
    lateinit var crashReporter: CrashReporter

    @Inject
    lateinit var appConfig: AppConfig

    override fun onCreate() {
        super.onCreate()
        
        // Initialize crash reporting
        crashReporter.logMessage("Application started")

        // Load application configuration
        appConfig.loadConfiguration()

        // Set up application monitoring
        setupApplicationMonitoring()
    }

    private fun setupApplicationMonitoring() {
        // Add application monitoring setup here
        // Example:
        // AppMonitor.initialize(this)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        crashReporter.logMessage("Low memory warning received")
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        crashReporter.logMessage("Trim memory event received: level $level")
    }
}
