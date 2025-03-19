package com.example.indicab.api

import com.example.indicab.models.PaymentRequest
import com.example.indicab.models.PaymentResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface PaymentService {
    @POST("payments/process")
    suspend fun processPayment(@Body request: PaymentRequest): Response<PaymentResponse>

    @POST("payments/status")
    suspend fun checkPaymentStatus(@Body paymentId: String): Response<PaymentResponse>

    @POST("payments/history")
    suspend fun getTransactionHistory(@Body userId: String): Response<List<PaymentResponse>>
}
