package com.example.indicab.security

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        "secure_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    companion object {
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_TOKEN_EXPIRY = "token_expiry"

        @Volatile
        private var instance: TokenManager? = null

        fun initialize(context: Context) {
            if (instance == null) {
                synchronized(this) {
                    if (instance == null) {
                        instance = TokenManager(context)
                    }
                }
            }
        }

        fun getInstance(): TokenManager {
            return instance ?: throw IllegalStateException("TokenManager must be initialized first")
        }

        fun getAccessToken(): String {
            return getInstance().getAccessToken()
        }
    }

    fun saveTokens(accessToken: String, refreshToken: String, expiryTimeMillis: Long) {
        sharedPreferences.edit()
            .putString(KEY_ACCESS_TOKEN, accessToken)
            .putString(KEY_REFRESH_TOKEN, refreshToken)
            .putLong(KEY_TOKEN_EXPIRY, expiryTimeMillis)
            .apply()
    }

    fun getAccessToken(): String {
        return sharedPreferences.getString(KEY_ACCESS_TOKEN, "") ?: ""
    }

    fun getRefreshToken(): String {
        return sharedPreferences.getString(KEY_REFRESH_TOKEN, "") ?: ""
    }

    fun getTokenExpiry(): Long {
        return sharedPreferences.getLong(KEY_TOKEN_EXPIRY, 0)
    }

    fun clearTokens() {
        sharedPreferences.edit()
            .remove(KEY_ACCESS_TOKEN)
            .remove(KEY_REFRESH_TOKEN)
            .remove(KEY_TOKEN_EXPIRY)
            .apply()
    }

    fun isTokenExpired(): Boolean {
        val expiry = getTokenExpiry()
        return expiry > 0 && System.currentTimeMillis() >= expiry
    }
}
