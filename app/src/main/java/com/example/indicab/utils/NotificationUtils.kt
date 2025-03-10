package com.example.indicab.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await

object NotificationUtils {
    private const val TAG = "NotificationUtils"
    private const val PREF_NAME = "FCMPrefs"
    private const val KEY_FCM_TOKEN = "fcm_token"

    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun saveFCMToken(context: Context, token: String) {
        getSharedPreferences(context).edit().apply {
            putString(KEY_FCM_TOKEN, token)
            apply()
        }
        Log.d(TAG, "FCM token saved: $token")
    }

    fun getFCMToken(context: Context): String? {
        return getSharedPreferences(context).getString(KEY_FCM_TOKEN, null)
    }

    suspend fun refreshFCMToken(context: Context): String? {
        return try {
            val token = FirebaseMessaging.getInstance().token.await()
            saveFCMToken(context, token)
            token
        } catch (e: Exception) {
            Log.e(TAG, "Failed to refresh FCM token", e)
            null
        }
    }

    // Subscribe to topics for specific notifications
    suspend fun subscribeToRideUpdates(userId: String) {
        try {
            // Subscribe to user-specific topic
            FirebaseMessaging.getInstance().subscribeToTopic("user_$userId").await()
            Log.d(TAG, "Subscribed to ride updates for user: $userId")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to subscribe to ride updates", e)
        }
    }

    // Unsubscribe from topics
    suspend fun unsubscribeFromRideUpdates(userId: String) {
        try {
            FirebaseMessaging.getInstance().unsubscribeFromTopic("user_$userId").await()
            Log.d(TAG, "Unsubscribed from ride updates for user: $userId")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to unsubscribe from ride updates", e)
        }
    }
} 