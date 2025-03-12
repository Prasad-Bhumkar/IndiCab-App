package com.example.indicab.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "drivers")
data class Driver(
    @PrimaryKey
    val id: String = "DRV" + System.currentTimeMillis(),
    val userId: String,
    val firstName: String,
    val lastName: String,
    val phone: String,
    val email: String,
    val licenseNumber: String,
    val licenseExpiry: LocalDateTime,
    val status: DriverStatus = DriverStatus.OFFLINE,
    val currentLocation: Location? = null,
    val currentVehicle: Vehicle? = null,
    val rating: Float = 0f,
    val totalRides: Int = 0,
    val totalEarnings: Double = 0.0,
    val documents: List<DriverDocument> = emptyList(),
    val preferences: DriverPreferences = DriverPreferences(),
    val metadata: DriverMetadata? = null,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

enum class DriverStatus {
    OFFLINE,        // Not available for rides
    AVAILABLE,      // Ready to accept rides
    ON_TRIP,        // Currently on a ride
    ON_BREAK,       // Taking a break
    MAINTENANCE,    // Vehicle maintenance
    SUSPENDED       // Account suspended
}

@Entity(tableName = "driver_documents")
data class DriverDocument(
    @PrimaryKey
    val id: String = "DOC" + System.currentTimeMillis(),
    val driverId: String,
    val type: DocumentType,
    val number: String,
    val issuedBy: String,
    val issuedDate: LocalDateTime,
    val expiryDate: LocalDateTime,
    val status: DocumentStatus = DocumentStatus.PENDING,
    val verificationDate: LocalDateTime? = null,
    val verifiedBy: String? = null,
    val fileUrl: String,
    val metadata: DocumentMetadata? = null,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

enum class DocumentType {
    DRIVERS_LICENSE,
    VEHICLE_REGISTRATION,
    INSURANCE,
    BACKGROUND_CHECK,
    VEHICLE_INSPECTION,
    PROFILE_PHOTO,
    OTHER
}

enum class DocumentStatus {
    PENDING,        // Awaiting verification
    VERIFIED,       // Document verified
    EXPIRED,        // Document expired
    REJECTED,       // Document rejected
    MISSING         // Document not submitted
}

data class DocumentMetadata(
    val rejectionReason: String? = null,
    val verificationNotes: String? = null,
    val reminderSent: Boolean = false,
    val lastReminderDate: LocalDateTime? = null
)

@Entity(tableName = "vehicles")
data class Vehicle(
    @PrimaryKey
    val id: String = "VEH" + System.currentTimeMillis(),
    val driverId: String,
    val make: String,
    val model: String,
    val year: Int,
    val color: String,
    val licensePlate: String,
    val type: VehicleType,
    val status: VehicleStatus = VehicleStatus.ACTIVE,
    val lastInspection: LocalDateTime? = null,
    val nextInspectionDue: LocalDateTime? = null,
    val metadata: VehicleMetadata? = null,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

enum class VehicleType {
    ECONOMY,        // Standard cars
    COMFORT,        // Premium cars
    XL,             // SUVs/Vans
    LUXURY,         // Luxury vehicles
    ELECTRIC        // Electric vehicles
}

enum class VehicleStatus {
    ACTIVE,         // Vehicle in service
    MAINTENANCE,    // Under maintenance
    INSPECTION,     // Due for inspection
    RETIRED,        // No longer in service
    SUSPENDED       // Temporarily suspended
}

data class VehicleMetadata(
    val mileage: Double? = null,
    val fuelType: String? = null,
    val lastService: LocalDateTime? = null,
    val nextServiceDue: LocalDateTime? = null,
    val insuranceExpiry: LocalDateTime? = null,
    val notes: String? = null
)

data class DriverPreferences(
    val maxDistance: Double = 20.0,         // Maximum pickup distance in km
    val preferredAreas: List<String> = emptyList(), // Preferred operating areas
    val workingHours: WorkingHours = WorkingHours(),
    val breakPreferences: BreakPreferences = BreakPreferences(),
    val notifications: NotificationPreferences = NotificationPreferences()
)

data class WorkingHours(
    val weekdayStart: String = "09:00",
    val weekdayEnd: String = "18:00",
    val weekendStart: String = "10:00",
    val weekendEnd: String = "17:00",
    val maxHoursPerDay: Int = 12,
    val maxHoursPerWeek: Int = 60,
    val preferredDays: List<Int> = listOf(1, 2, 3, 4, 5) // 1 = Monday
)

data class BreakPreferences(
    val breakDuration: Int = 30,            // Minutes
    val breakInterval: Int = 240,           // Minutes between breaks
    val autoBreak: Boolean = true,          // Automatically schedule breaks
    val preferredBreakTime: String = "13:00"
)

data class NotificationPreferences(
    val newRideAlerts: Boolean = true,
    val earningsUpdates: Boolean = true,
    val breakReminders: Boolean = true,
    val maintenanceReminders: Boolean = true,
    val documentExpiry: Boolean = true
)

data class DriverMetadata(
    val lastActive: LocalDateTime? = null,
    val lastBreak: LocalDateTime? = null,
    val currentShiftStart: LocalDateTime? = null,
    val totalHoursToday: Double = 0.0,
    val totalHoursThisWeek: Double = 0.0,
    val lastEarningsPayout: LocalDateTime? = null,
    val backgroundCheckExpiry: LocalDateTime? = null,
    val trainingCompletionDate: LocalDateTime? = null
)

// Driver earnings
@Entity(tableName = "driver_earnings")
data class DriverEarnings(
    @PrimaryKey
    val id: String = "ERN" + System.currentTimeMillis(),
    val driverId: String,
    val period: EarningsPeriod,
    val startDate: LocalDateTime,
    val endDate: LocalDateTime,
    val totalEarnings: Double = 0.0,
    val rideEarnings: Double = 0.0,
    val bonusEarnings: Double = 0.0,
    val tips: Double = 0.0,
    val totalRides: Int = 0,
    val totalHours: Double = 0.0,
    val status: EarningsStatus = EarningsStatus.PENDING,
    val metadata: EarningsMetadata? = null,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

enum class EarningsPeriod {
    DAILY,
    WEEKLY,
    MONTHLY,
    CUSTOM
}

enum class EarningsStatus {
    PENDING,        // Not yet processed
    PROCESSING,     // Being processed
    PAID,          // Payment completed
    FAILED         // Payment failed
}

data class EarningsMetadata(
    val paymentMethod: String? = null,
    val paymentReference: String? = null,
    val paymentDate: LocalDateTime? = null,
    val taxDeductions: Double = 0.0,
    val platformFees: Double = 0.0,
    val incentives: List<Incentive> = emptyList()
)

data class Incentive(
    val type: String,
    val description: String,
    val amount: Double,
    val criteria: String? = null,
    val expiryDate: LocalDateTime? = null
)

// Driver performance metrics
@Entity(tableName = "driver_performance")
data class DriverPerformance(
    @PrimaryKey
    val id: String = "PERF" + System.currentTimeMillis(),
    val driverId: String,
    val period: PerformancePeriod,
    val startDate: LocalDateTime,
    val endDate: LocalDateTime,
    val metrics: PerformanceMetrics,
    val rating: Float = 0f,
    val status: PerformanceStatus = PerformanceStatus.GOOD,
    val feedback: List<PerformanceFeedback> = emptyList(),
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

enum class PerformancePeriod {
    DAILY,
    WEEKLY,
    MONTHLY,
    QUARTERLY
}

enum class PerformanceStatus {
    EXCELLENT,
    GOOD,
    FAIR,
    POOR,
    CRITICAL
}

data class PerformanceMetrics(
    val acceptanceRate: Float = 0f,
    val cancellationRate: Float = 0f,
    val completionRate: Float = 0f,
    val onTimeRate: Float = 0f,
    val averageResponseTime: Double = 0.0,
    val averageRating: Float = 0f,
    val complaints: Int = 0,
    val compliments: Int = 0,
    val safetyScore: Float = 0f,
    val totalRides: Int = 0,
    val totalHours: Double = 0.0,
    val earnings: Double = 0.0
)

data class PerformanceFeedback(
    val type: FeedbackType,
    val message: String,
    val severity: FeedbackSeverity,
    val date: LocalDateTime = LocalDateTime.now()
)

enum class FeedbackType {
    ACCEPTANCE_RATE,
    CANCELLATION_RATE,
    RATING,
    SAFETY,
    CUSTOMER_SERVICE,
    VEHICLE_CONDITION
}

enum class FeedbackSeverity {
    POSITIVE,
    NEUTRAL,
    WARNING,
    CRITICAL
}