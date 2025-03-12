package com.example.indicab.data.repositories

import com.example.indicab.services.DriverService
import com.example.indicab.models.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.LocalDateTime

class DriverRepository(
    private val driverService: DriverService
) {
    // Driver profile operations
    suspend fun getDriverProfile(driverId: String): Flow<DriverProfile> = flow {
        emit(driverService.getDriverProfile(driverId))
    }

    suspend fun updateDriverProfile(profile: DriverProfile) = flow {
        emit(driverService.updateDriverProfile(profile))
    }

    // Vehicle management
    suspend fun getVehicleDetails(driverId: String) = flow {
        emit(driverService.getVehicleDetails(driverId))
    }

    suspend fun updateVehicleDetails(details: VehicleDetails) = flow {
        emit(driverService.updateVehicleDetails(details))
    }

    // Earnings and payments
    suspend fun getEarnings(
        driverId: String,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ) = flow {
        emit(driverService.getEarnings(driverId, startDate, endDate))
    }

    suspend fun getDailyStats(driverId: String) = flow {
        emit(driverService.getDailyStats(driverId))
    }

    // Document management
    suspend fun getDocuments(driverId: String) = flow {
        emit(driverService.getDocuments(driverId))
    }

    suspend fun uploadDocument(document: DriverDocument) = flow {
        emit(driverService.uploadDocument(document))
    }

    // Performance metrics
    suspend fun getPerformanceMetrics(
        driverId: String,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ) = flow {
        emit(driverService.getPerformanceMetrics(driverId, startDate, endDate))
    }

    suspend fun getRatings(driverId: String) = flow {
        emit(driverService.getRatings(driverId))
    }

    // Availability management
    suspend fun updateAvailabilityStatus(
        driverId: String,
        status: DriverStatus
    ) = flow {
        emit(driverService.updateAvailabilityStatus(driverId, status))
    }

    suspend fun getSchedule(
        driverId: String,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ) = flow {
        emit(driverService.getSchedule(driverId, startDate, endDate))
    }

    suspend fun updateSchedule(schedule: DriverSchedule) = flow {
        emit(driverService.updateSchedule(schedule))
    }

    // State management
    sealed class DriverState {
        object Loading : DriverState()
        data class Success<T>(val data: T) : DriverState()
        data class Error(val message: String) : DriverState()
    }

    companion object {
        private const val TAG = "DriverRepository"
    }
}

// Data classes for driver-specific operations
data class VehicleDetails(
    val id: String,
    val driverId: String,
    val make: String,
    val model: String,
    val year: Int,
    val licensePlate: String,
    val color: String,
    val vehicleType: String,
    val registrationNumber: String,
    val insuranceNumber: String,
    val lastInspectionDate: LocalDateTime,
    val nextInspectionDue: LocalDateTime,
    val status: VehicleStatus,
    val documents: List<VehicleDocument>,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

enum class VehicleStatus {
    ACTIVE,
    MAINTENANCE,
    INSPECTION_DUE,
    SUSPENDED,
    INACTIVE
}

data class VehicleDocument(
    val id: String,
    val vehicleId: String,
    val type: String,
    val number: String,
    val issuedDate: LocalDateTime,
    val expiryDate: LocalDateTime,
    val documentUrl: String,
    val status: DocumentStatus,
    val verifiedAt: LocalDateTime? = null,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

enum class DocumentStatus {
    PENDING,
    VERIFIED,
    REJECTED,
    EXPIRED
}

data class DriverSchedule(
    val id: String,
    val driverId: String,
    val weeklySchedule: List<DailySchedule>,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

data class DailySchedule(
    val dayOfWeek: Int,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val status: ScheduleStatus
)

enum class ScheduleStatus {
    AVAILABLE,
    BUSY,
    OFF_DUTY,
    ON_BREAK
}

enum class DriverStatus {
    ONLINE,
    OFFLINE,
    ON_TRIP,
    ON_BREAK,
    MAINTENANCE
}
