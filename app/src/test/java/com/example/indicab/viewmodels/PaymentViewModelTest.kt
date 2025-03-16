package com.example.indicab.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.indicab.models.PaymentGatewayResponse
import com.example.indicab.models.PaymentRequest
import com.example.indicab.services.PaymentService
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.*

class PaymentViewModelTest {
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var paymentViewModel: PaymentViewModel
    private lateinit var paymentService: PaymentService

    @Before
    fun setUp() {
        paymentService = mock(PaymentService::class.java)
        paymentViewModel = PaymentViewModel(paymentService, "userId123")
    }

    @Test
    fun testProcessPayment_Success() = runBlocking {
        val request = PaymentRequest(
            amount = 100.0,
            paymentMethodId = "valid_payment_method",
            bookingId = "booking123"
        )
        val response = PaymentGatewayResponse(success = true, transactionId = "txn123", amount = 100.0, currency = "INR", status = TransactionStatus.COMPLETED)

        `when`(paymentService.processPayment(request)).thenReturn(response)

        paymentViewModel.processPayment(100.0)

        assertTrue(paymentViewModel.paymentState.value is PaymentViewModel.PaymentState.Success)
    }

    @Test
    fun testProcessPayment_Failure() = runBlocking {
        val request = PaymentRequest(
            amount = 100.0,
            paymentMethodId = "invalid_payment_method",
            bookingId = "booking123"
        )
        val response = PaymentGatewayResponse(success = false, transactionId = null, amount = 100.0, currency = "INR", status = TransactionStatus.FAILED, errorMessage = "Invalid payment method")

        `when`(paymentService.processPayment(request)).thenReturn(response)

        paymentViewModel.processPayment(100.0)

        assertTrue(paymentViewModel.paymentState.value is PaymentViewModel.PaymentState.Error)
    }
}
