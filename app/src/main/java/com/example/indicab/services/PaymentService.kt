<<<<<<< HEAD
 package com.example.indicab.services
 
 import com.example.indicab.data.dao.PaymentMethodDao
 import com.example.indicab.data.dao.TransactionDao
 import com.example.indicab.data.dao.WalletDao
 import com.example.indicab.models.*
 import kotlinx.coroutines.flow.Flow
 import kotlinx.coroutines.flow.first
 import java.time.LocalDateTime
 import javax.inject.Inject
 import javax.inject.Singleton
 
 @Singleton
 class PaymentService @Inject constructor(
     private val paymentMethodDao: PaymentMethodDao,
     private val transactionDao: TransactionDao,
     private val walletDao: WalletDao,
     private val paymentGateway: PaymentGateway
 ) {
     suspend fun processPayment(request: PaymentRequest): PaymentGatewayResponse {
         return try {
             // Create initial transaction record
             val transaction = Transaction(
                 userId = request.userId,
                 bookingId = request.bookingId,
                 amount = request.amount,
                 currency = request.currency,
                 type = TransactionType.PAYMENT,
                 status = TransactionStatus.PENDING,
                 paymentMethodId = request.paymentMethodId
             )
             transactionDao.insertTransaction(transaction)
 
             // Process payment through gateway
             val response = when (request.paymentMethod.type) {
                 PaymentMethodType.WALLET -> processWalletPayment(request)
                 else -> paymentGateway.processPayment(request)
             }
 
             // Update transaction with gateway response
             transactionDao.updateTransactionStatus(
                 transactionId = transaction.id,
                 status = response.status,
                 metadata = TransactionMetadata(
                     gatewayTransactionId = response.transactionId,
                     gatewayResponse = response.toString()
                 )
             )
 
             // Handle split payments if applicable
             if (request.isSplitPayment && request.splitDetails != null) {
                 processSplitPayment(request, response)
             }
 
             response
         } catch (e: Exception) {
             PaymentGatewayResponse(
                 success = false,
                 transactionId = null,
                 amount = request.amount,
                 currency = request.currency,
                 status = TransactionStatus.FAILED,
                 errorMessage = e.message
             )
         }
     }
 
     private suspend fun processWalletPayment(request: PaymentRequest): PaymentGatewayResponse {
         val wallet = walletDao.getWallet(request.userId).first()
         return if (wallet != null && wallet.balance >= request.amount) {
             walletDao.updateBalance(request.userId, -request.amount)
             PaymentGatewayResponse(
                 success = true,
                 transactionId = "WAL" + System.currentTimeMillis(),
                 amount = request.amount,
                 currency = request.currency,
                 status = TransactionStatus.COMPLETED
             )
         } else {
             PaymentGatewayResponse(
                 success = false,
                 transactionId = null,
                 amount = request.amount,
                 currency = request.currency,
                 status = TransactionStatus.FAILED,
                 errorMessage = "Insufficient wallet balance"
             )
         }
     }
 
     private suspend fun processSplitPayment(
         request: PaymentRequest,
         mainPaymentResponse: PaymentGatewayResponse
     ) {
         request.splitDetails?.splits?.forEach { split ->
             val splitTransaction = Transaction(
                 userId = split.userId,
                 bookingId = request.bookingId,
                 amount = split.amount,
                 currency = request.currency,
                 type = TransactionType.SPLIT_PAYMENT,
                 status = TransactionStatus.PENDING,
                 paymentMethodId = request.paymentMethodId,
                 metadata = TransactionMetadata(
                     splitDetails = request.splitDetails
                 )
             )
             transactionDao.insertTransaction(splitTransaction)
         }
     }
 
     suspend fun processRefund(
         transactionId: String,
         amount: Double,
         reason: String
     ): PaymentGatewayResponse {
         val transaction = transactionDao.getTransactionById(transactionId)
         return if (transaction != null) {
             val refundResponse = paymentGateway.processRefund(
                 originalTransactionId = transactionId,
                 amount = amount
             )
 
             // Create refund transaction
             val refundTransaction = Transaction(
                 userId = transaction.userId,
                 bookingId = transaction.bookingId,
                 amount = amount,
                 currency = transaction.currency,
                 type = TransactionType.REFUND,
                 status = refundResponse.status,
                 paymentMethodId = transaction.paymentMethodId,
                 metadata = TransactionMetadata(
                     gatewayTransactionId = refundResponse.transactionId,
                     gatewayResponse = refundResponse.toString(),
                     refundReason = reason
                 )
             )
             transactionDao.insertTransaction(refundTransaction)
 
             // If it was a wallet payment, refund to wallet
             if (transaction.paymentMethod.type == PaymentMethodType.WALLET) {
                 walletDao.updateBalance(transaction.userId, amount)
             }
 
             refundResponse
         } else {
             PaymentGatewayResponse(
                 success = false,
                 transactionId = null,
                 amount = amount,
                 currency = "INR",
                 status = TransactionStatus.FAILED,
                 errorMessage = "Original transaction not found"
             )
         }
     }
 
     fun getTransactionHistory(userId: String): Flow<List<Transaction>> =
         transactionDao.getAllTransactions(userId)
 
     fun getWalletBalance(userId: String): Flow<Wallet?> =
         walletDao.getWallet(userId)
 
     suspend fun addPaymentMethod(
         userId: String,
         type: PaymentMethodType,
         name: String,
         details: PaymentMethodDetails,
         setAsDefault: Boolean = false
     ): PaymentMethod {
         val paymentMethod = PaymentMethod(
             userId = userId,
             type = type,
             name = name,
             details = details,
             isDefault = setAsDefault
         )
         
         if (setAsDefault) {
             paymentMethodDao.clearDefaultPaymentMethod(userId)
         }
         
         paymentMethodDao.insertPaymentMethod(paymentMethod)
         return paymentMethod
     }
 
     fun getActivePaymentMethods(userId: String): Flow<List<PaymentMethod>> =
         paymentMethodDao.getActivePaymentMethods(userId)
 
     suspend fun setDefaultPaymentMethod(userId: String, paymentMethodId: String) =
         paymentMethodDao.setDefaultPaymentMethod(userId, paymentMethodId)
 
     suspend fun removePaymentMethod(paymentMethod: PaymentMethod) =
         paymentMethodDao.deletePaymentMethod(paymentMethod)
 }
 
 // Interface for actual payment gateway implementation
 interface PaymentGateway {
     suspend fun processPayment(request: PaymentRequest): PaymentGatewayResponse
     suspend fun processRefund(originalTransactionId: String, amount: Double): PaymentGatewayResponse
     suspend fun checkTransactionStatus(transactionId: String): PaymentGatewayResponse
 }
=======
package com.example.indicab.services

import com.example.indicab.data.dao.PaymentMethodDao
import com.example.indicab.data.dao.TransactionDao
import com.example.indicab.data.dao.WalletDao
import com.example.indicab.models.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton
import android.util.Log
import com.example.indicab.analytics.AnalyticsManager

@Singleton
class PaymentService @Inject constructor(
    private val paymentMethodDao: PaymentMethodDao,
    private val transactionDao: TransactionDao,
    private val walletDao: WalletDao,
    private val paymentGateway: PaymentGateway
) {
    private val maxRetries = 3
    private val retryDelay = 2000L

    suspend fun processPayment(request: PaymentRequest): PaymentGatewayResponse {
        AnalyticsManager.logEvent("payment_processing", mapOf("userId" to request.userId, "amount" to request.amount.toString()))
        Log.d("PaymentService", "Processing payment for userId=${request.userId}, bookingId=${request.bookingId}, amount=${request.amount}")

        if (request.paymentMethodId.isEmpty() || request.amount <= 0) {
            return PaymentGatewayResponse(
                errorCode = "INVALID_PAYMENT_METHOD",
                success = false,
                transactionId = null,
                amount = request.amount,
                currency = request.currency,
                status = TransactionStatus.FAILED,
                errorMessage = "Invalid payment method details."
            )
        }

        val transaction = Transaction(
            userId = request.userId,
            bookingId = request.bookingId,
            amount = request.amount,
            currency = request.currency,
            type = TransactionType.PAYMENT,
            status = TransactionStatus.PENDING,
            paymentMethodId = request.paymentMethodId
        )
        transactionDao.insertTransaction(transaction)

        val response = when (request.paymentMethod.type) {
            PaymentMethodType.WALLET -> processWalletPayment(request)
            else -> paymentGateway.processPayment(request)
        }

        transactionDao.updateTransactionStatus(
            transactionId = transaction.id,
            status = response.status,
            metadata = TransactionMetadata(
                gatewayTransactionId = response.transactionId,
                gatewayResponse = response.toString()
            )
        )

        if (request.isSplitPayment && request.splitDetails != null) {
            processSplitPayment(request, response)
        }

        return response
    }

    private suspend fun processWalletPayment(request: PaymentRequest): PaymentGatewayResponse {
        val wallet = walletDao.getWallet(request.userId).first()
        Log.d("PaymentService", "Processing wallet payment for userId=${request.userId}, amount=${request.amount}, currentBalance=${wallet?.balance}")
        return if (wallet != null && wallet.balance >= request.amount) {
            walletDao.updateBalance(request.userId, -request.amount)
            PaymentGatewayResponse(
                success = true,
                transactionId = "WAL" + System.currentTimeMillis(),
                amount = request.amount,
                currency = request.currency,
                status = TransactionStatus.COMPLETED
            )
        } else {
            Log.e("PaymentService", "Insufficient wallet balance for userId=${request.userId}, attemptedAmount=${request.amount}")
            PaymentGatewayResponse(
                success = false,
                transactionId = null,
                amount = request.amount,
                currency = request.currency,
                status = TransactionStatus.FAILED,
                errorMessage = "Insufficient wallet balance"
            )
        }
    }

    private suspend fun processSplitPayment(
        request: PaymentRequest,
        mainPaymentResponse: PaymentGatewayResponse
    ) {
        request.splitDetails?.splits?.forEach { split ->
            val splitTransaction = Transaction(
                userId = split.userId,
                bookingId = request.bookingId,
                amount = split.amount,
                currency = request.currency,
                type = TransactionType.SPLIT_PAYMENT,
                status = TransactionStatus.PENDING,
                paymentMethodId = request.paymentMethodId,
                metadata = TransactionMetadata(
                    splitDetails = request.splitDetails
                )
            )
            transactionDao.insertTransaction(splitTransaction)
        }
    }

    suspend fun processRefund(
        transactionId: String,
        amount: Double,
        reason: String
    ): PaymentGatewayResponse {
        Log.d("PaymentService", "Processing refund for transactionId=$transactionId, amount=$amount")
        val transaction = transactionDao.getTransactionById(transactionId)
        return if (transaction != null) {
            val refundResponse = paymentGateway.processRefund(
                originalTransactionId = transactionId,
                amount = amount
            )

            val refundTransaction = Transaction(
                userId = transaction.userId,
                bookingId = transaction.bookingId,
                amount = amount,
                currency = transaction.currency,
                type = TransactionType.REFUND,
                status = refundResponse.status,
                paymentMethodId = transaction.paymentMethodId,
                metadata = TransactionMetadata(
                    gatewayTransactionId = refundResponse.transactionId,
                    gatewayResponse = refundResponse.toString(),
                    refundReason = reason
                )
            )
            transactionDao.insertTransaction(refundTransaction)

            if (transaction.paymentMethod.type == PaymentMethodType.WALLET) {
                walletDao.updateBalance(transaction.userId, amount)
            }

            refundResponse
        } else {
            Log.e("PaymentService", "Original transaction not found for transactionId=$transactionId")
            PaymentGatewayResponse(
                success = false,
                transactionId = null,
                amount = amount,
                currency = "INR",
                status = TransactionStatus.FAILED,
                errorMessage = "Original transaction not found"
            )
        }
    }

    fun getTransactionHistory(userId: String): Flow<List<Transaction>> =
        transactionDao.getAllTransactions(userId)

    fun getWalletBalance(userId: String): Flow<Wallet?> =
        walletDao.getWallet(userId)

    suspend fun addPaymentMethod(
        userId: String,
        type: PaymentMethodType,
        name: String,
        details: PaymentMethodDetails,
        setAsDefault: Boolean = false
    ): PaymentMethod {
        val paymentMethod = PaymentMethod(
            userId = userId,
            type = type,
            name = name,
            details = details,
            isDefault = setAsDefault
        )
        
        if (setAsDefault) {
            paymentMethodDao.clearDefaultPaymentMethod(userId)
        }
        
        paymentMethodDao.insertPaymentMethod(paymentMethod)
        return paymentMethod
    }

    fun getActivePaymentMethods(userId: String): Flow<List<PaymentMethod>> =
        paymentMethodDao.getActivePaymentMethods(userId)

    suspend fun setDefaultPaymentMethod(userId: String, paymentMethodId: String) =
        paymentMethodDao.setDefaultPaymentMethod(userId, paymentMethodId)

    suspend fun removePaymentMethod(paymentMethod: PaymentMethod) =
        paymentMethodDao.deletePaymentMethod(paymentMethod)
}
>>>>>>> 81ec31f166cdb0573d5c5135fcdecb0f6ba49d83
