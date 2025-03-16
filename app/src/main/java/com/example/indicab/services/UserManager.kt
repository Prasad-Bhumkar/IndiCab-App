 package com.example.indicab.services
 
 import android.content.Context
 import androidx.datastore.core.DataStore
 import androidx.datastore.preferences.core.Preferences
 import androidx.datastore.preferences.core.edit
 import androidx.datastore.preferences.core.stringPreferencesKey
 import androidx.datastore.preferences.preferencesDataStore
 import kotlinx.coroutines.flow.Flow
 import kotlinx.coroutines.flow.map
 import javax.inject.Inject
 import javax.inject.Singleton
 
 private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")
 
 @Singleton
 class UserManager @Inject constructor(
     private val context: Context
 ) {
     companion object {
         private val USER_ID_KEY = stringPreferencesKey("user_id")
     }
 
     suspend fun setUserId(userId: String) {
         context.dataStore.edit { preferences ->
             preferences[USER_ID_KEY] = userId
         }
     }
 
     fun getUserId(): Flow<String?> {
         return context.dataStore.data.map { preferences ->
             preferences[USER_ID_KEY]
         }
     }
 }
