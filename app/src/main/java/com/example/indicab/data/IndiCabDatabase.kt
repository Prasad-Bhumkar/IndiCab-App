package com.example.indicab.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.indicab.data.dao.*
import com.example.indicab.models.*
import java.time.LocalDateTime

@Database(
    entities = [
        FavoriteLocation::class,
        Wallet::class,
        PaymentMethod::class,
        Transaction::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteLocationDao(): FavoriteLocationDao
    abstract fun walletDao(): WalletDao
    abstract fun paymentMethodDao(): PaymentMethodDao
    abstract fun transactionDao(): TransactionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "indicab_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

// Repository classes
class FavoriteLocationRepository(private val favoriteLocationDao: FavoriteLocationDao) {
    fun getAllFavoriteLocations(userId: String) = 
        favoriteLocationDao.getAllFavoriteLocations(userId)

    fun getFavoriteLocationsByType(userId: String, type: FavoriteLocationType) =
        favoriteLocationDao.getFavoriteLocationsByType(userId, type)

    fun searchFavoriteLocations(userId: String, query: String) =
        favoriteLocationDao.searchFavoriteLocations(userId, query)

    fun getRecentFavoriteLocations(userId: String, limit: Int = 5) =
        favoriteLocationDao.getRecentFavoriteLocations(userId, limit)

    fun getHomeAndWorkLocations(userId: String) =
        favoriteLocationDao.getHomeAndWorkLocations(userId)

    fun getAllTags(userId: String) = 
        favoriteLocationDao.getAllTags(userId)

    suspend fun addFavoriteLocation(location: FavoriteLocation) =
        favoriteLocationDao.addOrUpdateFavoriteLocation(location)

    suspend fun updateFavoriteLocation(location: FavoriteLocation) =
        favoriteLocationDao.updateFavoriteLocation(location)

    suspend fun deleteFavoriteLocation(location: FavoriteLocation) =
        favoriteLocationDao.deleteFavoriteLocation(location)

    suspend fun incrementUsageCount(id: String, userId: String) =
        favoriteLocationDao.incrementUsageCount(id, userId)

    suspend fun getFavoriteLocationById(id: String, userId: String) =
        favoriteLocationDao.getFavoriteLocationById(id, userId)
}

class PaymentRepository(
    private val walletDao: WalletDao,
    private val paymentMethodDao: PaymentMethodDao,
    private val transactionDao: TransactionDao
) {
    // Wallet operations
    fun getWallet(userId: String) = walletDao.getWallet(userId)
    
    suspend fun updateWalletBalance(userId: String, amount: Double) =
        walletDao.updateBalance(userId, amount)
    
    suspend fun processWalletTransaction(userId: String, amount: Double) =
        walletDao.processTransaction(userId, amount)

    // Payment method operations
    fun getActivePaymentMethods(userId: String) =
        paymentMethodDao.getActivePaymentMethods(userId)
    
    suspend fun addPaymentMethod(paymentMethod: PaymentMethod) =
        paymentMethodDao.insertPaymentMethod(paymentMethod)
    
    suspend fun updatePaymentMethod(paymentMethod: PaymentMethod) =
        paymentMethodDao.updatePaymentMethod(paymentMethod)
    
    suspend fun removePaymentMethod(paymentMethod: PaymentMethod) =
        paymentMethodDao.deletePaymentMethod(paymentMethod)
    
    suspend fun setDefaultPaymentMethod(userId: String, paymentMethodId: String) =
        paymentMethodDao.setDefaultPaymentMethod(userId, paymentMethodId)

    // Transaction operations
    fun getAllTransactions(userId: String) =
        transactionDao.getAllTransactions(userId)
    
    fun getTransactionsByType(userId: String, type: TransactionType) =
        transactionDao.getTransactionsByType(userId, type)
    
    fun getTransactionsForBooking(userId: String, bookingId: String) =
        transactionDao.getTransactionsForBooking(userId, bookingId)
    
    suspend fun addTransaction(transaction: Transaction) =
        transactionDao.insertTransaction(transaction)
    
    suspend fun updateTransactionStatus(
        transactionId: String,
        status: TransactionStatus,
        metadata: TransactionMetadata? = null
    ) = transactionDao.updateTransactionStatus(transactionId, status, metadata)

    fun getTotalAmountForPeriod(
        userId: String,
        type: TransactionType,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ) = transactionDao.getTotalAmountForPeriod(userId, type, TransactionStatus.COMPLETED, startDate, endDate)
}
