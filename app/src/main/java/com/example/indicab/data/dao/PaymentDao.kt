 package com.example.indicab.data.dao
 
 import androidx.room.*
 import com.example.indicab.models.*
 import kotlinx.coroutines.flow.Flow
 import java.time.LocalDateTime
 
 @Dao
 interface WalletDao {
     @Query("SELECT * FROM wallets WHERE userId = :userId")
     fun getWallet(userId: String): Flow<Wallet?>
 
     @Insert(onConflict = OnConflictStrategy.REPLACE)
     suspend fun insertWallet(wallet: Wallet)
 
     @Update
     suspend fun updateWallet(wallet: Wallet)
 
     @Query("""
         UPDATE wallets 
         SET balance = balance + :amount,
             updatedAt = :timestamp
         WHERE userId = :userId
     """)
     suspend fun updateBalance(userId: String, amount: Double, timestamp: LocalDateTime = LocalDateTime.now())
 
     @Transaction
     suspend fun processTransaction(userId: String, amount: Double): Boolean {
         val wallet = getWallet(userId).value
         return if (wallet != null && wallet.balance + amount >= 0) {
             updateBalance(userId, amount)
             true
         } else {
             false
         }
     }
 }
 
 @Dao
 interface PaymentMethodDao {
     @Query("SELECT * FROM payment_methods WHERE userId = :userId AND isEnabled = 1")
     fun getActivePaymentMethods(userId: String): Flow<List<PaymentMethod>>
 
     @Query("SELECT * FROM payment_methods WHERE id = :id AND userId = :userId")
     suspend fun getPaymentMethodById(id: String, userId: String): PaymentMethod?
 
     @Query("SELECT * FROM payment_methods WHERE userId = :userId AND isDefault = 1 LIMIT 1")
     suspend fun getDefaultPaymentMethod(userId: String): PaymentMethod?
 
     @Insert(onConflict = OnConflictStrategy.REPLACE)
     suspend fun insertPaymentMethod(paymentMethod: PaymentMethod)
 
     @Update
     suspend fun updatePaymentMethod(paymentMethod: PaymentMethod)
 
     @Delete
     suspend fun deletePaymentMethod(paymentMethod: PaymentMethod)
 
     @Query("UPDATE payment_methods SET isDefault = 0 WHERE userId = :userId")
     suspend fun clearDefaultPaymentMethod(userId: String)
 
     @Transaction
     suspend fun setDefaultPaymentMethod(userId: String, paymentMethodId: String) {
         clearDefaultPaymentMethod(userId)
         val paymentMethod = getPaymentMethodById(paymentMethodId, userId)
         paymentMethod?.let {
             updatePaymentMethod(it.copy(isDefault = true))
         }
     }
 }
 
 @Dao
 interface TransactionDao {
     @Query("SELECT * FROM transactions WHERE userId = :userId ORDER BY createdAt DESC")
     fun getAllTransactions(userId: String): Flow<List<Transaction>>
 
     @Query("""
         SELECT * FROM transactions 
         WHERE userId = :userId 
         AND type = :type 
         ORDER BY createdAt DESC
     """)
     fun getTransactionsByType(userId: String, type: TransactionType): Flow<List<Transaction>>
 
     @Query("""
         SELECT * FROM transactions 
         WHERE userId = :userId 
         AND bookingId = :bookingId 
         ORDER BY createdAt DESC
     """)
     fun getTransactionsForBooking(userId: String, bookingId: String): Flow<List<Transaction>>
 
     @Query("""
         SELECT * FROM transactions 
         WHERE userId = :userId 
         AND createdAt >= :startDate 
         AND createdAt <= :endDate 
         ORDER BY createdAt DESC
     """)
     fun getTransactionsInDateRange(
         userId: String,
         startDate: LocalDateTime,
         endDate: LocalDateTime
     ): Flow<List<Transaction>>
 
     @Insert(onConflict = OnConflictStrategy.REPLACE)
     suspend fun insertTransaction(transaction: Transaction)
 
     @Update
     suspend fun updateTransaction(transaction: Transaction)
 
     @Query("SELECT * FROM transactions WHERE id = :id")
     suspend fun getTransactionById(id: String): Transaction?
 
     @Transaction
     suspend fun updateTransactionStatus(
         transactionId: String,
         status: TransactionStatus,
         metadata: TransactionMetadata? = null
     ) {
         val transaction = getTransactionById(transactionId)
         transaction?.let {
             updateTransaction(
                 it.copy(
                     status = status,
                     metadata = metadata ?: it.metadata,
                     updatedAt = LocalDateTime.now()
                 )
             )
         }
     }
 
     @Query("""
         SELECT SUM(amount) 
         FROM transactions 
         WHERE userId = :userId 
         AND type = :type 
         AND status = :status 
         AND createdAt >= :startDate 
         AND createdAt <= :endDate
     """)
     fun getTotalAmountForPeriod(
         userId: String,
         type: TransactionType,
         status: TransactionStatus = TransactionStatus.COMPLETED,
         startDate: LocalDateTime,
         endDate: LocalDateTime
     ): Flow<Double?>
 
     @Query("""
         SELECT * FROM transactions 
         WHERE status = :status 
         AND createdAt <= :timeout
     """)
     suspend fun getPendingTransactions(
         status: TransactionStatus = TransactionStatus.PENDING,
         timeout: LocalDateTime = LocalDateTime.now().minusMinutes(30)
     ): List<Transaction>
 }
