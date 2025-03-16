package com.example.indicab.data.dao

import androidx.room.*
import com.example.indicab.models.*
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions WHERE userId = :userId ORDER BY createdAt DESC")
    fun getAllTransactions(userId: String): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE userId = :userId AND type = :type ORDER BY createdAt DESC")
    fun getTransactionsByType(userId: String, type: TransactionType): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE userId = :userId AND bookingId = :bookingId ORDER BY createdAt DESC")
    fun getTransactionsForBooking(userId: String, bookingId: String): Flow<List<Transaction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: Transaction)

    @Update
    suspend fun updateTransaction(transaction: Transaction)

    @Delete
    suspend fun deleteTransaction(transaction: Transaction)

    @Query("""
        SELECT * FROM transactions 
        WHERE userId = :userId 
        AND type = :type 
        AND status = :status 
        AND createdAt BETWEEN :startDate AND :endDate
        ORDER BY createdAt DESC
    """)
    fun getTransactionsForPeriod(
        userId: String,
        type: TransactionType,
        status: TransactionStatus,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): Flow<List<Transaction>>

    @Query("""
        SELECT SUM(amount) FROM transactions 
        WHERE userId = :userId 
        AND type = :type 
        AND status = :status 
        AND createdAt BETWEEN :startDate AND :endDate
    """)
    fun getTotalAmountForPeriod(
        userId: String,
        type: TransactionType,
        status: TransactionStatus,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): Flow<Double>

    @Query("UPDATE transactions SET status = :status, metadata = :metadata WHERE id = :transactionId")
    suspend fun updateTransactionStatus(
        transactionId: String,
        status: TransactionStatus,
        metadata: TransactionMetadata?
    )

    @Query("SELECT * FROM transactions WHERE id = :id AND userId = :userId")
    suspend fun getTransactionById(id: String, userId: String): Transaction?

    @Transaction
    suspend fun processRefund(originalTransaction: Transaction, refundAmount: Double) {
        val refund = Transaction(
            userId = originalTransaction.userId,
            bookingId = originalTransaction.bookingId,
            amount = refundAmount,
            type = TransactionType.REFUND,
            status = TransactionStatus.COMPLETED,
            paymentMethodId = originalTransaction.paymentMethodId,
            description = "Refund for transaction ${originalTransaction.id}",
            metadata = TransactionMetadata(
                refundReason = "Customer requested refund"
            )
        )
        insertTransaction(refund)
        updateTransactionStatus(
            originalTransaction.id,
            TransactionStatus.REFUNDED,
            TransactionMetadata(refundReason = "Customer requested refund")
        )
    }

    @Transaction
    suspend fun processSplitPayment(splitPaymentDetails: SplitPaymentDetails) {
        splitPaymentDetails.splits.forEach { split ->
            val transaction = Transaction(
                userId = split.userId,
                amount = split.amount,
                type = TransactionType.SPLIT_PAYMENT,
                status = TransactionStatus.PENDING,
                paymentMethodId = "", // Will be set when user selects payment method
                description = "Split payment initiated by ${splitPaymentDetails.initiatedBy}",
                metadata = TransactionMetadata(
                    splitDetails = splitPaymentDetails
                )
            )
            insertTransaction(transaction)
        }
    }
}
