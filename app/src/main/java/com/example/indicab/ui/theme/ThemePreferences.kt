 package com.example.indicab.ui.theme
 
 import android.content.Context
 import androidx.datastore.core.DataStore
 import androidx.datastore.preferences.core.*
 import androidx.datastore.preferences.preferencesDataStore
 import kotlinx.coroutines.flow.Flow
 import kotlinx.coroutines.flow.catch
 import kotlinx.coroutines.flow.map
 import java.io.IOException
 import javax.inject.Inject
 import javax.inject.Singleton
 
 private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "theme_preferences")
 
 enum class ThemeMode {
     LIGHT,
     DARK,
     SYSTEM
 }
 
 data class ThemePreference(
     val themeMode: ThemeMode = ThemeMode.SYSTEM,
     val isDynamicColorEnabled: Boolean = true
 )
 
 @Singleton
 class ThemePreferencesManager @Inject constructor(
     private val context: Context
 ) {
     private object PreferencesKeys {
         val THEME_MODE = stringPreferencesKey("theme_mode")
         val DYNAMIC_COLOR_ENABLED = booleanPreferencesKey("dynamic_color_enabled")
     }
 
     val themePreferencesFlow: Flow<ThemePreference> = context.dataStore.data
         .catch { exception ->
             if (exception is IOException) {
                 emit(emptyPreferences())
             } else {
                 throw exception
             }
         }
         .map { preferences ->
             ThemePreference(
                 themeMode = ThemeMode.valueOf(
                     preferences[PreferencesKeys.THEME_MODE] ?: ThemeMode.SYSTEM.name
                 ),
                 isDynamicColorEnabled = preferences[PreferencesKeys.DYNAMIC_COLOR_ENABLED] ?: true
             )
         }
 
     suspend fun updateThemeMode(themeMode: ThemeMode) {
         context.dataStore.edit { preferences ->
             preferences[PreferencesKeys.THEME_MODE] = themeMode.name
         }
     }
 
     suspend fun updateDynamicColorEnabled(enabled: Boolean) {
         context.dataStore.edit { preferences ->
             preferences[PreferencesKeys.DYNAMIC_COLOR_ENABLED] = enabled
         }
     }
 
     suspend fun resetToDefaults() {
         context.dataStore.edit { preferences ->
             preferences.clear()
         }
     }
 }
 
 @Singleton
 class ThemeController @Inject constructor(
     private val themePreferencesManager: ThemePreferencesManager
 ) {
     val themePreferencesFlow = themePreferencesManager.themePreferencesFlow
 
     suspend fun setThemeMode(themeMode: ThemeMode) {
         themePreferencesManager.updateThemeMode(themeMode)
     }
 
     suspend fun setDynamicColorEnabled(enabled: Boolean) {
         themePreferencesManager.updateDynamicColorEnabled(enabled)
     }
 
     suspend fun resetToDefaults() {
         themePreferencesManager.resetToDefaults()
     }
 
     fun shouldUseDarkTheme(themePreference: ThemePreference): Boolean {
         return when (themePreference.themeMode) {
             ThemeMode.LIGHT -> false
             ThemeMode.DARK -> true
             ThemeMode.SYSTEM -> isSystemInDarkTheme()
         }
     }
 
     private fun isSystemInDarkTheme(): Boolean {
         // This would normally use the system's dark theme setting
         // For now, we'll return false as a default
         return false
     }
 }
 
 // Hilt module for theme-related dependencies
 @dagger.Module
 @dagger.hilt.InstallIn(dagger.hilt.components.SingletonComponent::class)
 object ThemeModule {
     @dagger.Provides
     @Singleton
     fun provideThemePreferencesManager(
         @dagger.hilt.android.qualifiers.ApplicationContext context: Context
     ): ThemePreferencesManager {
         return ThemePreferencesManager(context)
     }
 
     @dagger.Provides
     @Singleton
     fun provideThemeController(
         themePreferencesManager: ThemePreferencesManager
     ): ThemeController {
         return ThemeController(themePreferencesManager)
     }
 }
