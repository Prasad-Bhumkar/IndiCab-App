package com.example.indicab

import android.app.Application
import com.google.android.libraries.places.api.Places

class IndiCabApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Initialize Places SDK for location autocomplete
        val apiKey = BuildConfig.MAPS_API_KEY
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, apiKey)
        }
    }

    companion object {
        private const val TAG = "IndiCabApplication"
    }
}
