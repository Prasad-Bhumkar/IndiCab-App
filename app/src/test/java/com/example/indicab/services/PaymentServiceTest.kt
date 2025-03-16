package com.example.indicab.services

import com.example.indicab.models.*
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*

class PaymentServiceTest {
    private lateinit var paymentService: PaymentService
    private lateinit var paymentMethodDao: PaymentMethodDao
    private lateinit var transactionDao: TransactionDao
    private lateinit var walletDao: WalletDao
    private lateinit var paymentGateway: PaymentGateway

    @Before
    fun setUp() {
        paymentMethodDao = mock(PaymentMethodDao::class.java)
        transactionDao = mock(TransactionDao::class.java)
        walletDao = mock(WalletDao::class.java)
        paymentGateway = mock(PaymentGateway::class.java)

        paymentService = PaymentService(paymentMethodDao, transactionDao, walletDao, paymentGateway)
    }

    @Test
    fun testProcessPayment_Success() = runBlocking {
        val request = PaymentRequest(
            amount = 100.0,
            paymentMethodId = "valid_payment_method",
            bookingId = "booking123"
        )
        val response = PaymentGatewayResponse(success = true, transactionId = "txn123", amount = 100.0, currency = "INR", status = TransactionStatus.COMPLETED)

        `when`(paymentGateway.processPayment(request)).thenReturn(response)

        val result = paymentService.processPayment(request)

        assertTrue(result.success)
        assertEquals("txn123", result.transactionId)
    }

    @Test
    fun testProcessPayment_Failure() = runBlocking {
        val request = PaymentRequest(
            amount = 100.0,
            paymentMethodId = "invalid_payment_method",
            bookingId = "booking123"
        )
        val response = PaymentGatewayResponse(success = false, transactionId = null, amount = 100.0, currency = "INR", status = TransactionStatus.FAILED, errorMessage = "Invalid payment method")

        `when`(paymentGateway.processPayment(request)).thenReturn(response)

        val result = paymentService.processPayment(request)

        assertFalse(result.success)
        assertEquals("Invalid payment method", result.errorMessage)
    }
}
