package com.example.indicab

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class IndiCabApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize any necessary components here
    }
}
