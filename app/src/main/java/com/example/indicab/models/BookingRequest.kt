package com.example.indicab.models

data class BookingRequest(
    val id: String = "",
    val userId: String = "",
    val carTypeId: String = "",
    val pickupLocation: Location = Location(),
    val dropLocation: Location = Location(),
    val pickupTime: Long = 0L,
    val tripType: String = "ONE_WAY",
    val status: String = "PENDING",
    val fareDetails: FareDetails? = null,
    val createdAt: Long = System.currentTimeMillis(),
    // Additional fields used in HomeScreenActivity
    val date: String = "",
    val time: String = "",
    val carType: String = "",
    val passengers: Int = 0
) 