package com.example.indicab.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "emergency_contacts")
data class EmergencyContact(
    @PrimaryKey
    val id: String = "EC" + System.currentTimeMillis(),
    val userId: String,
    val name: String,
    val phone: String,
    val relationship: String,
    val priority: Int = 0,
    val notifyOnSOS: Boolean = true,
    val notifyOnRideStart: Boolean = false,
    val notifyOnRideEnd: Boolean = false,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

@Entity(tableName = "sos_alerts")
data class SOSAlert(
    @PrimaryKey
    val id: String = "SOS" + System.currentTimeMillis(),
    val userId: String,
    val bookingId: String?,
    val location: Location,
    val type: SOSType,
    val status: SOSStatus = SOSStatus.ACTIVE,
    val description: String? = null,
    val metadata: SOSMetadata? = null,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

enum class SOSType {
    MEDICAL,        // Medical emergency
    SECURITY,       // Security threat
    ACCIDENT,       // Vehicle accident
    BREAKDOWN,      // Vehicle breakdown
    OTHER           // Other emergency
}

enum class SOSStatus {
    ACTIVE,         // Alert is active
    RESPONDING,     // Help is on the way
    RESOLVED,       // Situation resolved
    CANCELLED       // Alert cancelled
}

data class SOSMetadata(
    val speed: Double? = null,
    val heading: Double? = null,
    val batteryLevel: Int? = null,
    val networkType: String? = null,
    val signalStrength: Int? = null,
    val nearestLandmark: String? = null,
    val vehicleDetails: VehicleDetails? = null,
    val lastKnownLocation: Location? = null,
    val responderId: String? = null,
    val responderETA: LocalDateTime? = null
)

@Entity(tableName = "incidents")
data class Incident(
    @PrimaryKey
    val id: String = "INC" + System.currentTimeMillis(),
    val userId: String,
    val bookingId: String?,
    val type: IncidentType,
    val status: IncidentStatus = IncidentStatus.REPORTED,
    val location: Location,
    val description: String,
    val photos: List<String> = emptyList(),
    val metadata: IncidentMetadata? = null,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

enum class IncidentType {
    HARASSMENT,     // Harassment or misconduct
    DANGEROUS_DRIVING, // Reckless or dangerous driving
    ROUTE_DEVIATION, // Unexpected route deviation
    VEHICLE_ISSUE,  // Vehicle-related issues
    SERVICE_QUALITY, // Service quality concerns
    OTHER           // Other incidents
}

enum class IncidentStatus {
    REPORTED,       // Initial report
    INVESTIGATING,  // Under investigation
    RESOLVED,       // Issue resolved
    CLOSED,         // Case closed
    ESCALATED       // Escalated to authorities
}

data class IncidentMetadata(
    val reportedBy: String,
    val assignedTo: String? = null,
    val priority: IncidentPriority = IncidentPriority.MEDIUM,
    val resolution: String? = null,
    val actionTaken: String? = null,
    val followUpRequired: Boolean = false,
    val followUpDate: LocalDateTime? = null
)

enum class IncidentPriority {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL
}

@Entity(tableName = "safety_checks")
data class SafetyCheck(
    @PrimaryKey
    val id: String = "SC" + System.currentTimeMillis(),
    val userId: String,
    val bookingId: String?,
    val type: SafetyCheckType,
    val status: SafetyCheckStatus,
    val response: Boolean? = null,
    val responseTime: LocalDateTime? = null,
    val metadata: SafetyCheckMetadata? = null,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

enum class SafetyCheckType {
    PERIODIC,       // Regular interval check
    DEVIATION,      // Route deviation check
    SPEED,          // Excessive speed check
    STOP,          // Long stop check
    BATTERY,        // Low battery check
    MANUAL          // Manually triggered check
}

enum class SafetyCheckStatus {
    PENDING,        // Awaiting response
    RESPONDED,      // User responded
    EXPIRED,        // No response received
    ESCALATED       // Escalated to emergency
}

data class SafetyCheckMetadata(
    val triggerReason: String? = null,
    val location: Location? = null,
    val speed: Double? = null,
    val batteryLevel: Int? = null,
    val expectedRoute: List<Location>? = null,
    val actualRoute: List<Location>? = null
)

// Location sharing
@Entity(tableName = "location_shares")
data class LocationShare(
    @PrimaryKey
    val id: String = "LS" + System.currentTimeMillis(),
    val userId: String,
    val bookingId: String?,
    val shareCode: String,
    val recipientType: ShareRecipientType,
    val recipientId: String? = null,
    val expiresAt: LocalDateTime,
    val lastAccessed: LocalDateTime? = null,
    val status: ShareStatus = ShareStatus.ACTIVE,
    val metadata: ShareMetadata? = null,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

enum class ShareRecipientType {
    CONTACT,        // Emergency contact
    PUBLIC,         // Public share link
    AUTHORITY       // Law enforcement/emergency services
}

enum class ShareStatus {
    ACTIVE,         // Share is active
    EXPIRED,        // Share has expired
    REVOKED         // Share was revoked
}

data class ShareMetadata(
    val accessCount: Int = 0,
    val lastAccessIP: String? = null,
    val lastAccessDevice: String? = null,
    val shareLink: String? = null,
    val restrictions: ShareRestrictions? = null
)

data class ShareRestrictions(
    val maxAccesses: Int? = null,
    val allowedIPs: List<String>? = null,
    val requireAuth: Boolean = false,
    val showHistory: Boolean = true,
    val showETA: Boolean = true
)

// Emergency settings
@Entity(tableName = "emergency_settings")
data class EmergencySettings(
    @PrimaryKey
    val userId: String,
    val sosEnabled: Boolean = true,
    val autoSOS: Boolean = true,
    val autoSOSDelay: Int = 30, // seconds
    val locationShareDuration: Int = 24, // hours
    val notifyContacts: Boolean = true,
    val notifyAuthorities: Boolean = true,
    val safetyChecksEnabled: Boolean = true,
    val safetyCheckInterval: Int = 30, // minutes
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)
