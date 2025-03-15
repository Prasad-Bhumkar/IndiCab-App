package com.example.indicab

import android.app.Application
import com.google.android.libraries.places.api.Places
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class IndiCabApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize Places SDK
        Places.initialize(applicationContext, BuildConfig.MAPS_API_KEY)
    }
}
