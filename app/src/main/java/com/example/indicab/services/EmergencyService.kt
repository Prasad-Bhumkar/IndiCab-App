package com.example.indicab.services

import android.content.Context
import android.location.Location
import android.telephony.SmsManager
import com.example.indicab.data.dao.*
import com.example.indicab.models.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EmergencyService @Inject constructor(
    private val context: Context,
    private val emergencyContactDao: EmergencyContactDao,
    private val sosAlertDao: SOSAlertDao,
    private val incidentDao: IncidentDao,
    private val safetyCheckDao: SafetyCheckDao,
    private val locationShareDao: LocationShareDao,
    private val emergencySettingsDao: EmergencySettingsDao,
    private val locationService: LocationService,
    private val notificationService: NotificationService
) {
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val smsManager = SmsManager.getDefault()

    init {
        monitorSafetyChecks()
        monitorLocationShares()
    }

    private fun monitorSafetyChecks() {
        serviceScope.launch {
            safetyCheckDao.getPendingChecks()
                .collect { checks ->
                    checks.forEach { check ->
                        if (check.createdAt.plusMinutes(5) < LocalDateTime.now()) {
                            // Auto-escalate if no response after 5 minutes
                            escalateToSOS(
                                userId = check.userId,
                                bookingId = check.bookingId,
                                type = SOSType.OTHER,
                                description = "No response to safety check"
                            )
                        }
                    }
                }
        }
    }

    private fun monitorLocationShares() {
        serviceScope.launch {
            locationShareDao.getActiveShares()
                .collect { shares ->
                    shares.forEach { share ->
                        if (share.expiresAt < LocalDateTime.now()) {
                            locationShareDao.updateShareStatus(
                                shareId = share.id,
                                status = ShareStatus.EXPIRED
                            )
                        }
                    }
                }
        }
    }

    suspend fun triggerSOS(
        userId: String,
        bookingId: String?,
        type: SOSType,
        description: String? = null
    ): SOSAlert {
        val location = locationService.getCurrentLocation()
        val alert = SOSAlert(
            userId = userId,
            bookingId = bookingId,
            location = location,
            type = type,
            description = description,
            metadata = SOSMetadata(
                speed = locationService.getCurrentSpeed(),
                batteryLevel = getBatteryLevel(),
                networkType = getNetworkType(),
                signalStrength = getSignalStrength(),
                nearestLandmark = locationService.getNearestLandmark(location)
            )
        )
        
        sosAlertDao.insertAlert(alert)

        // Notify emergency contacts
        val settings = emergencySettingsDao.getEmergencySettings(userId).first()
        if (settings?.notifyContacts == true) {
            notifyEmergencyContacts(userId, alert)
        }

        // Notify authorities if enabled
        if (settings?.notifyAuthorities == true) {
            notifyAuthorities(alert)
        }

        // Create location share
        createEmergencyLocationShare(userId, bookingId)

        // Show notification
        notificationService.showEmergencyNotification(alert)

        return alert
    }

    private suspend fun notifyEmergencyContacts(userId: String, alert: SOSAlert) {
        val contacts = emergencyContactDao.getSosContacts(userId).first()
        contacts.forEach { contact ->
            sendSmsWithRetry(contact.phone, createSOSMessage(alert, contact))
        }
    }

    private suspend fun sendSmsWithRetry(phone: String, message: String, maxRetries: Int = 3) {
        var attempt = 0
        while (attempt < maxRetries) {
            try {
                smsManager.sendTextMessage(phone, null, message, null, null)
                return // Exit if successful
            } catch (e: Exception) {
                Log.e("EmergencyService", "Failed to send SMS to $phone, attempt ${attempt + 1}", e)
                attempt++
                delay(2000) // Wait before retrying
            }
        }
        Log.e("EmergencyService", "All attempts to send SMS to $phone failed.")
    }
    }

    private fun createSOSMessage(alert: SOSAlert, contact: EmergencyContact): String {
        val location = alert.location
        val locationUrl = "https://maps.google.com/?q=${location.latitude},${location.longitude}"
        return """
            EMERGENCY ALERT: ${contact.name}, ${alert.userId} needs help!
            Type: ${alert.type}
            Location: $locationUrl
            ${alert.description ?: ""}
            Track live location: ${createTrackingLink(alert.id)}
        """.trimIndent()
    }

    private suspend fun notifyAuthorities(alert: SOSAlert) {
        // Example implementation to notify authorities
        val emergencyNumber = "112" // Replace with actual emergency number
        val message = "Emergency alert: ${alert.userId} needs help!"
        sendSmsWithRetry(emergencyNumber, message)
    }

    private suspend fun createEmergencyLocationShare(
        userId: String,
        bookingId: String?
    ): LocationShare {
        val settings = emergencySettingsDao.getEmergencySettings(userId).first()
        val share = LocationShare(
            userId = userId,
            bookingId = bookingId,
            shareCode = generateShareCode(),
            recipientType = ShareRecipientType.AUTHORITY,
            expiresAt = LocalDateTime.now().plusHours(
                settings?.locationShareDuration?.toLong() ?: 24
            ),
            metadata = ShareMetadata(
                shareLink = createTrackingLink(userId)
            )
        )
        locationShareDao.insertLocationShare(share)
        return share
    }

    suspend fun reportIncident(
        userId: String,
        bookingId: String?,
        type: IncidentType,
        description: String,
        photos: List<String> = emptyList()
    ): Incident {
        val location = locationService.getCurrentLocation()
        val incident = Incident(
            userId = userId,
            bookingId = bookingId,
            type = type,
            location = location,
            description = description,
            photos = photos,
            metadata = IncidentMetadata(
                reportedBy = userId,
                priority = calculateIncidentPriority(type)
            )
        )
        incidentDao.insertIncident(incident)
        return incident
    }

    private fun calculateIncidentPriority(type: IncidentType): IncidentPriority {
        return when (type) {
            IncidentType.HARASSMENT -> IncidentPriority.HIGH
            IncidentType.DANGEROUS_DRIVING -> IncidentPriority.HIGH
            IncidentType.ROUTE_DEVIATION -> IncidentPriority.MEDIUM
            IncidentType.VEHICLE_ISSUE -> IncidentPriority.MEDIUM
            IncidentType.SERVICE_QUALITY -> IncidentPriority.LOW
            IncidentType.OTHER -> IncidentPriority.LOW
        }
    }

    suspend fun createSafetyCheck(
        userId: String,
        bookingId: String?,
        type: SafetyCheckType
    ): SafetyCheck {
        val location = locationService.getCurrentLocation()
        val check = SafetyCheck(
            userId = userId,
            bookingId = bookingId,
            type = type,
            status = SafetyCheckStatus.PENDING,
            metadata = SafetyCheckMetadata(
                location = location,
                speed = locationService.getCurrentSpeed(),
                batteryLevel = getBatteryLevel()
            )
        )
        safetyCheckDao.insertSafetyCheck(check)
        
        // Show notification
        notificationService.showSafetyCheckNotification(check)
        
        return check
    }

    suspend fun respondToSafetyCheck(
        checkId: String,
        response: Boolean
    ) {
        safetyCheckDao.updateCheckResponse(
            checkId = checkId,
            status = SafetyCheckStatus.RESPONDED,
            response = response
        )
    }

    suspend fun shareLocation(
        userId: String,
        bookingId: String?,
        recipientType: ShareRecipientType,
        recipientId: String? = null,
        duration: Int = 24
    ): LocationShare {
        val share = LocationShare(
            userId = userId,
            bookingId = bookingId,
            shareCode = generateShareCode(),
            recipientType = recipientType,
            recipientId = recipientId,
            expiresAt = LocalDateTime.now().plusHours(duration.toLong()),
            metadata = ShareMetadata(
                shareLink = createTrackingLink(userId)
            )
        )
        locationShareDao.insertLocationShare(share)
        return share
    }

    private fun generateShareCode(): String {
        return "SHARE" + System.currentTimeMillis()
    }

    private fun createTrackingLink(identifier: String): String {
        return "https://track.indicab.com/$identifier"
    }

    private fun getBatteryLevel(): Int {
        // Implementation to get device battery level
        return 100
    }

    private fun getNetworkType(): String {
        // Implementation to get network type
        return "4G"
    }

    private fun getSignalStrength(): Int {
        // Implementation to get signal strength
        return 4
    }

    private suspend fun escalateToSOS(
        userId: String,
        bookingId: String?,
        type: SOSType,
        description: String
    ) {
        val settings = emergencySettingsDao.getEmergencySettings(userId).first()
        if (settings?.autoSOS == true) {
            triggerSOS(userId, bookingId, type, description)
        }
    }

    fun getEmergencyContacts(userId: String): Flow<List<EmergencyContact>> =
        emergencyContactDao.getEmergencyContacts(userId)

    fun getActiveSOSAlerts(): Flow<List<SOSAlert>> =
        sosAlertDao.getActiveAlerts()

    fun getIncidents(userId: String): Flow<List<Incident>> =
        incidentDao.getIncidents(userId)

    fun getActiveLocationShares(userId: String): Flow<List<LocationShare>> =
        locationShareDao.getActiveShares()

    fun getEmergencySettings(userId: String): Flow<EmergencySettings?> =
        emergencySettingsDao.getEmergencySettings(userId)
}
