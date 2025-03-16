 package com.example.indicab.api
 
 import com.example.indicab.models.PaymentRequest
 import com.example.indicab.models.PaymentResponse
 import retrofit2.Response
 import retrofit2.http.Body
 import retrofit2.http.POST
 
 interface PaymentService {
     @Inject
     lateinit var monitoringService: MonitoringService
 
     @POST("payments/process")
     suspend fun processPayment(@Body request: PaymentRequest): Response<PaymentResponse> {
         val response = processPaymentInternal(request)
         
         // Track payment processing
         monitoringService.trackPayment(
             paymentId = response.body()?.paymentId ?: "unknown",
             amount = request.amount,
             status = if (response.isSuccessful) "success" else "failed"
         )
         
         return response
     }
 
     @POST("payments/status")
     suspend fun checkPaymentStatus(@Body paymentId: String): Response<PaymentResponse> {
         val response = checkPaymentStatusInternal(paymentId)
         
         // Track payment status check
         monitoringService.trackPayment(
             paymentId = paymentId,
             amount = response.body()?.amount ?: 0.0,
             status = response.body()?.status ?: "unknown"
         )
         
         return response
     }
 
     private suspend fun processPaymentInternal(request: PaymentRequest): Response<PaymentResponse> {
         // Original implementation
         return processPayment(request)
     }
 
     private suspend fun checkPaymentStatusInternal(paymentId: String): Response<PaymentResponse> {
         // Original implementation
         return checkPaymentStatus(paymentId)
     }
 }
