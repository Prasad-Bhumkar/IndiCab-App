package com.example.indicab.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "wallets")
data class Wallet(
    @PrimaryKey
    val userId: String,
    val balance: Double = 0.0,
    val currency: String = "INR",
    val isActive: Boolean = true,
    val autoReloadEnabled: Boolean = false,
    val autoReloadThreshold: Double = 100.0,
    val autoReloadAmount: Double = 500.0,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

@Entity(tableName = "payment_methods")
data class PaymentMethod(
    @PrimaryKey
    val id: String = "PM" + System.currentTimeMillis(),
    val userId: String,
    val type: PaymentMethodType,
    val name: String,
    val details: PaymentMethodDetails,
    val isDefault: Boolean = false,
    val isEnabled: Boolean = true,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

enum class PaymentMethodType {
    CREDIT_CARD,
    DEBIT_CARD,
    UPI,
    NET_BANKING,
    WALLET,
    CASH
}

data class PaymentMethodDetails(
    // Credit/Debit Card
    val cardNumber: String? = null,
    val cardHolderName: String? = null,
    val expiryMonth: Int? = null,
    val expiryYear: Int? = null,
    val cardNetwork: String? = null, // VISA, MASTERCARD, etc.
    
    // UPI
    val upiId: String? = null,
    val upiProvider: String? = null,
    
    // Net Banking
    val bankName: String? = null,
    val accountNumber: String? = null,
    val ifscCode: String? = null
)

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey
    val id: String = "TXN" + System.currentTimeMillis(),
    val statusCode: String? = null, // New field for status codes
    val errorMessage: String? = null, // New field for error messages
    val userId: String,
    val bookingId: String? = null,
    val amount: Double,
    val currency: String = "INR",
    val type: TransactionType,
    val status: TransactionStatus,
    val paymentMethodId: String,
    val description: String = "",
    val metadata: TransactionMetadata? = null,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

enum class TransactionType {
    PAYMENT,          // Ride payment
    REFUND,          // Refund for cancelled ride
    WALLET_RELOAD,   // Adding money to wallet
    WALLET_DEBIT,    // Using wallet money
    SPLIT_PAYMENT,   // Part of split payment
    CASHBACK,        // Rewards/cashback
    PENALTY          // Cancellation charges etc.
}

enum class TransactionStatus {
    PENDING,
    PROCESSING,
    COMPLETED,
    FAILED,
    REFUNDED,
    CANCELLED
}

data class TransactionMetadata(
    val gatewayTransactionId: String? = null,
    val gatewayResponse: String? = null,
    val splitDetails: SplitPaymentDetails? = null,
    val failureReason: String? = null,
    val refundReason: String? = null
)

data class SplitPaymentDetails(
    val totalAmount: Double,
    val splits: List<PaymentSplit>,
    val initiatedBy: String // userId
)

data class PaymentSplit(
    val userId: String,
    val amount: Double,
    val status: TransactionStatus,
    val transactionId: String? = null
)

// Payment Gateway Response
data class PaymentGatewayResponse(
    val success: Boolean,
    val transactionId: String?,
    val amount: Double,
    val currency: String,
    val status: TransactionStatus,
    val errorCode: String? = null,
    val errorMessage: String? = null,
    val gatewayReference: String? = null,
    val timestamp: LocalDateTime = LocalDateTime.now()
)

// Payment Request
data class PaymentRequest(
    val amount: Double,
    val currency: String = "INR",
    val paymentMethodId: String,
    val bookingId: String? = null,
    val metadata: Map<String, String> = emptyMap(),
    val isSplitPayment: Boolean = false,
    val splitDetails: SplitPaymentDetails? = null
)
