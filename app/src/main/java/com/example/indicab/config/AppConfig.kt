package com.example.indicab.config

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppConfig @Inject constructor(context: Context) {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_config")

    // Add configuration properties and methods here
    // Example:
    // val apiEndpoint: String
    // val isDebug: Boolean
    // val environment: EnvironmentType
}
