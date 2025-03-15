package com.example.indicab.api

import com.example.indicab.models.PaymentRequest
import com.example.indicab.models.PaymentResponse
import com.example.indicab.models.PaymentStatus
import retrofit2.Response
import javax.inject.Inject

class MockPaymentService @Inject constructor() : PaymentService {
    override suspend fun processPayment(request: PaymentRequest): Response<PaymentResponse> {
        // Simulate payment processing
        return Response.success(
            PaymentResponse(
                paymentId = "mock_payment_${System.currentTimeMillis()}",
                status = PaymentStatus.COMPLETED,
                amount = request.amount,
                currency = request.currency,
                timestamp = System.currentTimeMillis()
            )
        )
    }

    override suspend fun checkPaymentStatus(paymentId: String): Response<PaymentResponse> {
        // Simulate status check
        return Response.success(
            PaymentResponse(
                paymentId = paymentId,
                status = PaymentStatus.COMPLETED,
                amount = 0.0,
                currency = "USD",
                timestamp = System.currentTimeMillis()
            )
        )
    }
}
