package com.example.indicab.models

import android.net.Uri
import java.util.Date

data class DriverProfile(
    val id: String = "",
    val name: String = "",
    val phone: String = "",
    val email: String = "",
    val licenseNumber: String = "",
    val vehicleNumber: String = "",
    val vehicleModel: String = "",
    val vehicleColor: String = "",
    val rating: Double = 0.0,
    val isVerified: Boolean = false
)

enum class VerificationStatus {
    PENDING,
    IN_PROGRESS,
    APPROVED,
    REJECTED
} 