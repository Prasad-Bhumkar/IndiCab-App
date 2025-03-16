package com.example.indicab.data.repositories

import com.example.indicab.data.dao.WalletDao
import com.example.indicab.data.dao.PaymentMethodDao
import com.example.indicab.data.dao.TransactionDao
import com.example.indicab.models.*
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

class PaymentRepository(
    private val walletDao: WalletDao,
    private val paymentMethodDao: PaymentMethodDao,
    private val transactionDao: TransactionDao
) {
    // Wallet operations
    fun getWallet(userId: String): Flow<Wallet?> = walletDao.getWallet(userId)
    
    fun getActiveWallet(userId: String): Flow<Wallet?> = walletDao.getActiveWallet(userId)
    
    suspend fun updateWalletBalance(userId: String, amount: Double) =
        walletDao.updateBalance(userId, amount)
    
    suspend fun processWalletTransaction(userId: String, amount: Double) =
        walletDao.processTransaction(userId, amount)

    suspend fun createOrUpdateWallet(wallet: Wallet) =
        walletDao.createOrUpdateWallet(wallet)

    // Payment method operations
    fun getActivePaymentMethods(userId: String): Flow<List<PaymentMethod>> =
        paymentMethodDao.getActivePaymentMethods(userId)
    
    fun getDefaultPaymentMethod(userId: String): Flow<PaymentMethod?> =
        paymentMethodDao.getDefaultPaymentMethod(userId)
    
    suspend fun addPaymentMethod(paymentMethod: PaymentMethod) =
        paymentMethodDao.addOrUpdatePaymentMethod(paymentMethod)
    
    suspend fun updatePaymentMethod(paymentMethod: PaymentMethod) =
        paymentMethodDao.updatePaymentMethod(paymentMethod)
    
    suspend fun removePaymentMethod(paymentMethod: PaymentMethod) =
        paymentMethodDao.deletePaymentMethod(paymentMethod)
    
    suspend fun setDefaultPaymentMethod(userId: String, paymentMethodId: String) =
        paymentMethodDao.setDefaultPaymentMethod(userId, paymentMethodId)

    suspend fun disablePaymentMethod(id: String, userId: String) =
        paymentMethodDao.disablePaymentMethod(id, userId)

    // Transaction operations
    fun getAllTransactions(userId: String): Flow<List<Transaction>> =
        transactionDao.getAllTransactions(userId)
    
    fun getTransactionsByType(userId: String, type: TransactionType): Flow<List<Transaction>> =
        transactionDao.getTransactionsByType(userId, type)
    
    fun getTransactionsForBooking(userId: String, bookingId: String): Flow<List<Transaction>> =
        transactionDao.getTransactionsForBooking(userId, bookingId)
    
    suspend fun addTransaction(transaction: Transaction) =
        transactionDao.insertTransaction(transaction)
    
    suspend fun updateTransactionStatus(
        transactionId: String,
        status: TransactionStatus,
        metadata: TransactionMetadata? = null
    ) = transactionDao.updateTransactionStatus(transactionId, status, metadata)

    fun getTransactionsForPeriod(
        userId: String,
        type: TransactionType,
        status: TransactionStatus,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): Flow<List<Transaction>> = transactionDao.getTransactionsForPeriod(
        userId, type, status, startDate, endDate
    )

    fun getTotalAmountForPeriod(
        userId: String,
        type: TransactionType,
        status: TransactionStatus,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): Flow<Double> = transactionDao.getTotalAmountForPeriod(
        userId, type, status, startDate, endDate
    )

    suspend fun processRefund(originalTransaction: Transaction, refundAmount: Double) =
        transactionDao.processRefund(originalTransaction, refundAmount)

    suspend fun processSplitPayment(splitPaymentDetails: SplitPaymentDetails) =
        transactionDao.processSplitPayment(splitPaymentDetails)
}
