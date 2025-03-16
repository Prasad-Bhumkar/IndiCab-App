 package com.example.indicab.models
 
 import java.time.LocalDateTime
 
 data class ScheduledRide(
     val id: String = "",
     val userId: String = "",
     val bookingRequest: BookingRequest,
     val scheduledTime: LocalDateTime,
     val reminderEnabled: Boolean = true,
     val status: ScheduleStatus = ScheduleStatus.PENDING,
     val createdAt: LocalDateTime = LocalDateTime.now()
 )
 
 enum class ScheduleStatus {
     PENDING,
     CONFIRMED,
     CANCELLED,
     COMPLETED
 }
 
 data class ScheduleReminder(
     val rideId: String,
     val userId: String,
     val reminderTime: LocalDateTime, // Usually 30 mins before scheduled time
     val sent: Boolean = false
 )
