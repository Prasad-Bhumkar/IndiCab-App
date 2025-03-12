package com.example.indicab.data.dao

import androidx.room.*
import com.example.indicab.models.Wallet
import kotlinx.coroutines.flow.Flow

@Dao
interface WalletDao {
    @Query("SELECT * FROM wallets WHERE userId = :userId")
    fun getWallet(userId: String): Flow<Wallet?>

    @Query("UPDATE wallets SET balance = :amount WHERE userId = :userId")
    suspend fun updateBalance(userId: String, amount: Double)

    @Query("UPDATE wallets SET balance = balance + :amount WHERE userId = :userId")
    suspend fun processTransaction(userId: String, amount: Double)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWallet(wallet: Wallet)

    @Update
    suspend fun updateWallet(wallet: Wallet)

    @Delete
    suspend fun deleteWallet(wallet: Wallet)

    @Query("SELECT * FROM wallets WHERE userId = :userId AND isActive = 1")
    fun getActiveWallet(userId: String): Flow<Wallet?>

    @Transaction
    suspend fun createOrUpdateWallet(wallet: Wallet) {
        val existing = getWallet(wallet.userId).value
        if (existing != null) {
            updateWallet(wallet)
        } else {
            insertWallet(wallet)
        }
    }
}
