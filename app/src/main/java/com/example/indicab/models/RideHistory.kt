package com.example.indicab.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "ride_history")
data class RideHistory(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String,
    val bookingId: Long,
    val pickupLocation: Location,
    val dropLocation: Location,
    val carType: CarType,
    val fareDetails: FareDetails,
    val driver: Driver,
    val rating: com.example.indicab.models.Rating?,
    val review: String?,
    val timestamp: Date,
    val paymentMethod: String,
    val paymentStatus: PaymentStatus
)

enum class PaymentStatus {
    PENDING,
    COMPLETED,
    FAILED
}
