 package com.example.indicab.data.dao
 
 import androidx.room.*
 import com.example.indicab.models.*
 import kotlinx.coroutines.flow.Flow
 import java.time.LocalDateTime
 
 @Dao
 interface EmergencyContactDao {
     @Query("SELECT * FROM emergency_contacts WHERE userId = :userId ORDER BY priority DESC")
     fun getEmergencyContacts(userId: String): Flow<List<EmergencyContact>>
 
     @Query("SELECT * FROM emergency_contacts WHERE userId = :userId AND notifyOnSOS = 1")
     fun getSosContacts(userId: String): Flow<List<EmergencyContact>>
 
     @Insert(onConflict = OnConflictStrategy.REPLACE)
     suspend fun insertContact(contact: EmergencyContact)
 
     @Update
     suspend fun updateContact(contact: EmergencyContact)
 
     @Delete
     suspend fun deleteContact(contact: EmergencyContact)
 
     @Query("""
         UPDATE emergency_contacts 
         SET priority = :priority,
             updatedAt = :timestamp
         WHERE id = :contactId
     """)
     suspend fun updatePriority(
         contactId: String,
         priority: Int,
         timestamp: LocalDateTime = LocalDateTime.now()
     )
 }
 
 @Dao
 interface SOSAlertDao {
     @Query("SELECT * FROM sos_alerts WHERE userId = :userId ORDER BY createdAt DESC")
     fun getSOSAlerts(userId: String): Flow<List<SOSAlert>>
 
     @Query("SELECT * FROM sos_alerts WHERE status = :status ORDER BY createdAt DESC")
     fun getActiveAlerts(status: SOSStatus = SOSStatus.ACTIVE): Flow<List<SOSAlert>>
 
     @Insert(onConflict = OnConflictStrategy.REPLACE)
     suspend fun insertAlert(alert: SOSAlert)
 
     @Update
     suspend fun updateAlert(alert: SOSAlert)
 
     @Query("""
         UPDATE sos_alerts 
         SET status = :status,
             updatedAt = :timestamp
         WHERE id = :alertId
     """)
     suspend fun updateAlertStatus(
         alertId: String,
         status: SOSStatus,
         timestamp: LocalDateTime = LocalDateTime.now()
     )
 
     @Query("""
         UPDATE sos_alerts 
         SET metadata = :metadata,
             updatedAt = :timestamp
         WHERE id = :alertId
     """)
     suspend fun updateAlertMetadata(
         alertId: String,
         metadata: SOSMetadata,
         timestamp: LocalDateTime = LocalDateTime.now()
     )
 }
 
 @Dao
 interface IncidentDao {
     @Query("SELECT * FROM incidents WHERE userId = :userId ORDER BY createdAt DESC")
     fun getIncidents(userId: String): Flow<List<Incident>>
 
     @Query("""
         SELECT * FROM incidents 
         WHERE status IN (:statuses) 
         ORDER BY createdAt DESC
     """)
     fun getIncidentsByStatus(statuses: List<IncidentStatus>): Flow<List<Incident>>
 
     @Insert(onConflict = OnConflictStrategy.REPLACE)
     suspend fun insertIncident(incident: Incident)
 
     @Update
     suspend fun updateIncident(incident: Incident)
 
     @Query("""
         UPDATE incidents 
         SET status = :status,
             updatedAt = :timestamp
         WHERE id = :incidentId
     """)
     suspend fun updateIncidentStatus(
         incidentId: String,
         status: IncidentStatus,
         timestamp: LocalDateTime = LocalDateTime.now()
     )
 
     @Query("""
         UPDATE incidents 
         SET metadata = :metadata,
             updatedAt = :timestamp
         WHERE id = :incidentId
     """)
     suspend fun updateIncidentMetadata(
         incidentId: String,
         metadata: IncidentMetadata,
         timestamp: LocalDateTime = LocalDateTime.now()
     )
 }
 
 @Dao
 interface SafetyCheckDao {
     @Query("SELECT * FROM safety_checks WHERE userId = :userId ORDER BY createdAt DESC")
     fun getSafetyChecks(userId: String): Flow<List<SafetyCheck>>
 
     @Query("""
         SELECT * FROM safety_checks 
         WHERE status = :status 
         AND createdAt >= :since 
         ORDER BY createdAt DESC
     """)
     fun getPendingChecks(
         status: SafetyCheckStatus = SafetyCheckStatus.PENDING,
         since: LocalDateTime = LocalDateTime.now().minusHours(1)
     ): Flow<List<SafetyCheck>>
 
     @Insert(onConflict = OnConflictStrategy.REPLACE)
     suspend fun insertSafetyCheck(check: SafetyCheck)
 
     @Update
     suspend fun updateSafetyCheck(check: SafetyCheck)
 
     @Query("""
         UPDATE safety_checks 
         SET status = :status,
             response = :response,
             responseTime = :responseTime,
             updatedAt = :timestamp
         WHERE id = :checkId
     """)
     suspend fun updateCheckResponse(
         checkId: String,
         status: SafetyCheckStatus,
         response: Boolean,
         responseTime: LocalDateTime = LocalDateTime.now(),
         timestamp: LocalDateTime = LocalDateTime.now()
     )
 }
 
 @Dao
 interface LocationShareDao {
     @Query("SELECT * FROM location_shares WHERE userId = :userId ORDER BY createdAt DESC")
     fun getLocationShares(userId: String): Flow<List<LocationShare>>
 
     @Query("""
         SELECT * FROM location_shares 
         WHERE status = :status 
         AND expiresAt > :now 
         ORDER BY createdAt DESC
     """)
     fun getActiveShares(
         status: ShareStatus = ShareStatus.ACTIVE,
         now: LocalDateTime = LocalDateTime.now()
     ): Flow<List<LocationShare>>
 
     @Insert(onConflict = OnConflictStrategy.REPLACE)
     suspend fun insertLocationShare(share: LocationShare)
 
     @Update
     suspend fun updateLocationShare(share: LocationShare)
 
     @Query("""
         UPDATE location_shares 
         SET status = :status,
             updatedAt = :timestamp
         WHERE id = :shareId
     """)
     suspend fun updateShareStatus(
         shareId: String,
         status: ShareStatus,
         timestamp: LocalDateTime = LocalDateTime.now()
     )
 
     @Query("""
         UPDATE location_shares 
         SET lastAccessed = :timestamp,
             metadata = :metadata,
             updatedAt = :timestamp
         WHERE id = :shareId
     """)
     suspend fun updateShareAccess(
         shareId: String,
         metadata: ShareMetadata,
         timestamp: LocalDateTime = LocalDateTime.now()
     )
 }
 
 @Dao
 interface EmergencySettingsDao {
     @Query("SELECT * FROM emergency_settings WHERE userId = :userId")
     fun getEmergencySettings(userId: String): Flow<EmergencySettings?>
 
     @Insert(onConflict = OnConflictStrategy.REPLACE)
     suspend fun insertSettings(settings: EmergencySettings)
 
     @Update
     suspend fun updateSettings(settings: EmergencySettings)
 
     @Query("""
         UPDATE emergency_settings 
         SET sosEnabled = :enabled,
             updatedAt = :timestamp
         WHERE userId = :userId
     """)
     suspend fun updateSOSEnabled(
         userId: String,
         enabled: Boolean,
         timestamp: LocalDateTime = LocalDateTime.now()
     )
 
     @Query("""
         UPDATE emergency_settings 
         SET safetyChecksEnabled = :enabled,
             updatedAt = :timestamp
         WHERE userId = :userId
     """)
     suspend fun updateSafetyChecksEnabled(
         userId: String,
         enabled: Boolean,
         timestamp: LocalDateTime = LocalDateTime.now()
     )
 }
