package com.example.indicab.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "booking_history")
data class BookingHistory(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String,
    val pickupLocation: Location,
    val dropLocation: Location,
    val fareDetails: FareDetails,
    val timestamp: Date,
    val status: BookingStatus,
    val carType: CarType,
    val paymentMethodId: Long?
)

enum class BookingStatus {
    PENDING,
    CONFIRMED,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED
}
