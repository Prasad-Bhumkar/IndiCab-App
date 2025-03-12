package com.example.indicab.services

import com.example.indicab.models.*
import kotlinx.coroutines.delay
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class MockPaymentGateway @Inject constructor() : PaymentGateway {
    private val transactionStore = mutableMapOf<String, PaymentGatewayResponse>()
    private val failureRate = 0.1 // 10% chance of failure for testing

    override suspend fun processPayment(request: PaymentRequest): PaymentGatewayResponse {
        // Simulate network delay
        delay(Random.nextLong(500, 2000))

        // Simulate random failures
        if (Random.nextDouble() < failureRate) {
            return createFailureResponse(
                amount = request.amount,
                currency = request.currency,
                errorCode = "PAYMENT_FAILED",
                errorMessage = "Payment failed due to technical issues"
            )
        }

        // Simulate card validation
        if (request.paymentMethod.type in listOf(PaymentMethodType.CREDIT_CARD, PaymentMethodType.DEBIT_CARD)) {
            val isValid = validateCard(request.paymentMethod.details)
            if (!isValid) {
                return createFailureResponse(
                    amount = request.amount,
                    currency = request.currency,
                    errorCode = "INVALID_CARD",
                    errorMessage = "Invalid card details"
                )
            }
        }

        // Create successful response
        val response = PaymentGatewayResponse(
            success = true,
            transactionId = generateTransactionId(),
            amount = request.amount,
            currency = request.currency,
            status = TransactionStatus.COMPLETED,
            gatewayReference = "MOCK-${System.currentTimeMillis()}"
        )

        // Store transaction for future reference
        transactionStore[response.transactionId!!] = response
        return response
    }

    override suspend fun processRefund(
        originalTransactionId: String,
        amount: Double
    ): PaymentGatewayResponse {
        // Simulate network delay
        delay(Random.nextLong(500, 1500))

        // Check if original transaction exists
        val originalTransaction = transactionStore[originalTransactionId]
        if (originalTransaction == null) {
            return createFailureResponse(
                amount = amount,
                currency = "INR",
                errorCode = "TRANSACTION_NOT_FOUND",
                errorMessage = "Original transaction not found"
            )
        }

        // Validate refund amount
        if (amount > originalTransaction.amount) {
            return createFailureResponse(
                amount = amount,
                currency = originalTransaction.currency,
                errorCode = "INVALID_REFUND_AMOUNT",
                errorMessage = "Refund amount cannot exceed original payment amount"
            )
        }

        // Create successful refund response
        val response = PaymentGatewayResponse(
            success = true,
            transactionId = "REF-${originalTransactionId}",
            amount = amount,
            currency = originalTransaction.currency,
            status = TransactionStatus.COMPLETED,
            gatewayReference = "MOCK-REF-${System.currentTimeMillis()}"
        )

        // Store refund transaction
        transactionStore[response.transactionId!!] = response
        return response
    }

    override suspend fun checkTransactionStatus(transactionId: String): PaymentGatewayResponse {
        // Simulate network delay
        delay(Random.nextLong(200, 1000))

        return transactionStore[transactionId] ?: createFailureResponse(
            amount = 0.0,
            currency = "INR",
            errorCode = "TRANSACTION_NOT_FOUND",
            errorMessage = "Transaction not found"
        )
    }

    private fun validateCard(details: PaymentMethodDetails): Boolean {
        val cardNumber = details.cardNumber ?: return false
        val expiryMonth = details.expiryMonth ?: return false
        val expiryYear = details.expiryYear ?: return false

        // Basic validation
        if (cardNumber.length !in 13..19) return false
        if (expiryMonth !in 1..12) return false
        
        // Check expiry
        val now = LocalDateTime.now()
        val currentYear = now.year % 100
        val currentMonth = now.monthValue
        
        if (expiryYear < currentYear) return false
        if (expiryYear == currentYear && expiryMonth < currentMonth) return false

        // Luhn algorithm for card number validation
        return isValidLuhn(cardNumber)
    }

    private fun isValidLuhn(cardNumber: String): Boolean {
        var sum = 0
        var alternate = false
        
        // Loop through values starting from the rightmost digit
        for (i in cardNumber.length - 1 downTo 0) {
            var n = cardNumber[i].toString().toInt()
            if (alternate) {
                n *= 2
                if (n > 9) {
                    n = (n % 10) + 1
                }
            }
            sum += n
            alternate = !alternate
        }
        
        return (sum % 10 == 0)
    }

    private fun generateTransactionId(): String {
        return "MOCK-TXN-${System.currentTimeMillis()}-${Random.nextInt(1000, 9999)}"
    }

    private fun createFailureResponse(
        amount: Double,
        currency: String,
        errorCode: String,
        errorMessage: String
    ): PaymentGatewayResponse {
        return PaymentGatewayResponse(
            success = false,
            transactionId = null,
            amount = amount,
            currency = currency,
            status = TransactionStatus.FAILED,
            errorCode = errorCode,
            errorMessage = errorMessage,
            timestamp = LocalDateTime.now()
        )
    }

    // Test helper methods
    fun clearTransactionStore() {
        transactionStore.clear()
    }

    fun getStoredTransaction(transactionId: String): PaymentGatewayResponse? {
        return transactionStore[transactionId]
    }
}
