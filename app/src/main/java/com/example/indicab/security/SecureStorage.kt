package com.example.indicab.security

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SecureStorage @Inject constructor(context: Context) {

    private val keyStore = KeyStore.getInstance("AndroidKeyStore").apply {
        load(null)
    }

    private val keyAlias = "indiCabSecureKey"

    private fun getOrCreateKey(): SecretKey {
        if (!keyStore.containsAlias(keyAlias)) {
            val keyGenerator = KeyGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_AES,
                "AndroidKeyStore"
            )
            
            val keyGenSpec = KeyGenParameterSpec.Builder(
                keyAlias,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                .setUserAuthenticationRequired(true)
                .build()
            
            keyGenerator.init(keyGenSpec)
            keyGenerator.generateKey()
        }
        
        return keyStore.getKey(keyAlias, null) as SecretKey
    }

    fun encrypt(data: String): Pair<ByteArray, ByteArray> {
        val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding")
        cipher.init(Cipher.ENCRYPT_MODE, getOrCreateKey())
        val iv = cipher.iv
        val encrypted = cipher.doFinal(data.toByteArray())
        return Pair(encrypted, iv)
    }

    fun decrypt(encryptedData: ByteArray, iv: ByteArray): String {
        val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding")
        cipher.init(Cipher.DECRYPT_MODE, getOrCreateKey(), IvParameterSpec(iv))
        return String(cipher.doFinal(encryptedData))
    }

    fun storeData(key: String, value: String) {
        val (encryptedData, iv) = encrypt(value)
        // TODO: Store encryptedData and iv securely (e.g., in SharedPreferences or Room)
    }

    fun retrieveData(key: String): String? {
        // TODO: Retrieve encryptedData and iv from storage
        val encryptedData = ByteArray(0) // Replace with actual retrieval
        val iv = ByteArray(0) // Replace with actual retrieval
        return try {
            decrypt(encryptedData, iv)
        } catch (e: Exception) {
            null
        }
    }
}
