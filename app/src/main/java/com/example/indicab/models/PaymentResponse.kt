package com.example.indicab.models

data class PaymentResponse(
    val paymentId: String,
    val status: PaymentStatus,
    val amount: Double,
    val currency: String,
    val timestamp: Long
)
