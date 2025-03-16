 package com.example.indicab.viewmodels
 
 import androidx.lifecycle.ViewModel
 import androidx.lifecycle.ViewModelProvider
 import androidx.lifecycle.viewModelScope
 import com.example.indicab.models.*
 import com.example.indicab.services.EmergencyService
 import kotlinx.coroutines.flow.*
 import kotlinx.coroutines.launch
 import java.time.LocalDateTime
 
 class EmergencyViewModel(
     private val emergencyService: EmergencyService,
     private val userId: String,
     private val bookingId: String? = null
 ) : ViewModel() {
 
     private val _emergencyState = MutableStateFlow<EmergencyState>(EmergencyState.Initial)
     val emergencyState = _emergencyState.asStateFlow()
 
     private val _selectedEmergencyType = MutableStateFlow<SOSType?>(null)
     val selectedEmergencyType = _selectedEmergencyType.asStateFlow()
 
     private val _emergencyDescription = MutableStateFlow("")
     val emergencyDescription = _emergencyDescription.asStateFlow()
 
     val emergencyContacts = emergencyService.getEmergencyContacts(userId)
         .stateIn(
             scope = viewModelScope,
             started = SharingStarted.WhileSubscribed(5000),
             initialValue = emptyList()
         )
 
     val activeSOSAlerts = emergencyService.getActiveSOSAlerts()
         .stateIn(
             scope = viewModelScope,
             started = SharingStarted.WhileSubscribed(5000),
             initialValue = emptyList()
         )
 
     val emergencySettings = emergencyService.getEmergencySettings(userId)
         .stateIn(
             scope = viewModelScope,
             started = SharingStarted.WhileSubscribed(5000),
             initialValue = null
         )
 
     fun setEmergencyType(type: SOSType) {
         _selectedEmergencyType.value = type
     }
 
     fun setEmergencyDescription(description: String) {
         _emergencyDescription.value = description
     }
 
     fun triggerSOS() {
         viewModelScope.launch {
             try {
                 _emergencyState.value = EmergencyState.Processing
 
                 val type = _selectedEmergencyType.value
                     ?: throw IllegalStateException("Emergency type not selected")
 
                 val alert = emergencyService.triggerSOS(
                     userId = userId,
                     bookingId = bookingId,
                     type = type,
                     description = _emergencyDescription.value.takeIf { it.isNotBlank() }
                 )
 
                 _emergencyState.value = EmergencyState.SOSTriggered(alert)
             } catch (e: Exception) {
                 _emergencyState.value = EmergencyState.Error(
                     e.message ?: "Failed to trigger SOS"
                 )
             }
         }
     }
 
     fun reportIncident(
         type: IncidentType,
         description: String,
         photos: List<String> = emptyList()
     ) {
         viewModelScope.launch {
             try {
                 _emergencyState.value = EmergencyState.Processing
 
                 val incident = emergencyService.reportIncident(
                     userId = userId,
                     bookingId = bookingId,
                     type = type,
                     description = description,
                     photos = photos
                 )
 
                 _emergencyState.value = EmergencyState.IncidentReported(incident)
             } catch (e: Exception) {
                 _emergencyState.value = EmergencyState.Error(
                     e.message ?: "Failed to report incident"
                 )
             }
         }
     }
 
     fun shareLocation(
         recipientType: ShareRecipientType,
         recipientId: String? = null,
         duration: Int = 24
     ) {
         viewModelScope.launch {
             try {
                 _emergencyState.value = EmergencyState.Processing
 
                 val share = emergencyService.shareLocation(
                     userId = userId,
                     bookingId = bookingId,
                     recipientType = recipientType,
                     recipientId = recipientId,
                     duration = duration
                 )
 
                 _emergencyState.value = EmergencyState.LocationShared(share)
             } catch (e: Exception) {
                 _emergencyState.value = EmergencyState.Error(
                     e.message ?: "Failed to share location"
                 )
             }
         }
     }
 
     fun respondToSafetyCheck(checkId: String, isSafe: Boolean) {
         viewModelScope.launch {
             try {
                 _emergencyState.value = EmergencyState.Processing
 
                 emergencyService.respondToSafetyCheck(checkId, isSafe)
 
                 if (!isSafe) {
                     // Automatically trigger SOS if user indicates they're not safe
                     triggerSOS()
                 } else {
                     _emergencyState.value = EmergencyState.SafetyConfirmed
                 }
             } catch (e: Exception) {
                 _emergencyState.value = EmergencyState.Error(
                     e.message ?: "Failed to respond to safety check"
                 )
             }
         }
     }
 
     fun addEmergencyContact(
         name: String,
         phone: String,
         relationship: String,
         priority: Int = 0,
         notifyOnSOS: Boolean = true
     ) {
         viewModelScope.launch {
             try {
                 _emergencyState.value = EmergencyState.Processing
 
                 val contact = EmergencyContact(
                     userId = userId,
                     name = name,
                     phone = phone,
                     relationship = relationship,
                     priority = priority,
                     notifyOnSOS = notifyOnSOS
                 )
 
                 // This would be implemented in EmergencyService
                 // emergencyService.addEmergencyContact(contact)
 
                 _emergencyState.value = EmergencyState.ContactAdded(contact)
             } catch (e: Exception) {
                 _emergencyState.value = EmergencyState.Error(
                     e.message ?: "Failed to add emergency contact"
                 )
             }
         }
     }
 
     fun updateEmergencySettings(
         sosEnabled: Boolean? = null,
         autoSOS: Boolean? = null,
         autoSOSDelay: Int? = null,
         locationShareDuration: Int? = null,
         notifyContacts: Boolean? = null,
         notifyAuthorities: Boolean? = null,
         safetyChecksEnabled: Boolean? = null,
         safetyCheckInterval: Int? = null
     ) {
         viewModelScope.launch {
             try {
                 _emergencyState.value = EmergencyState.Processing
 
                 val currentSettings = emergencySettings.value ?: EmergencySettings(userId)
                 val updatedSettings = currentSettings.copy(
                     sosEnabled = sosEnabled ?: currentSettings.sosEnabled,
                     autoSOS = autoSOS ?: currentSettings.autoSOS,
                     autoSOSDelay = autoSOSDelay ?: currentSettings.autoSOSDelay,
                     locationShareDuration = locationShareDuration ?: currentSettings.locationShareDuration,
                     notifyContacts = notifyContacts ?: currentSettings.notifyContacts,
                     notifyAuthorities = notifyAuthorities ?: currentSettings.notifyAuthorities,
                     safetyChecksEnabled = safetyChecksEnabled ?: currentSettings.safetyChecksEnabled,
                     safetyCheckInterval = safetyCheckInterval ?: currentSettings.safetyCheckInterval,
                     updatedAt = LocalDateTime.now()
                 )
 
                 // This would be implemented in EmergencyService
                 // emergencyService.updateEmergencySettings(updatedSettings)
 
                 _emergencyState.value = EmergencyState.SettingsUpdated(updatedSettings)
             } catch (e: Exception) {
                 _emergencyState.value = EmergencyState.Error(
                     e.message ?: "Failed to update emergency settings"
                 )
             }
         }
     }
 
     fun resetState() {
         _emergencyState.value = EmergencyState.Initial
         _selectedEmergencyType.value = null
         _emergencyDescription.value = ""
     }
 
     class Factory(
         private val emergencyService: EmergencyService,
         private val userId: String,
         private val bookingId: String? = null
     ) : ViewModelProvider.Factory {
         @Suppress("UNCHECKED_CAST")
         override fun <T : ViewModel> create(modelClass: Class<T>): T {
             if (modelClass.isAssignableFrom(EmergencyViewModel::class.java)) {
                 return EmergencyViewModel(emergencyService, userId, bookingId) as T
             }
             throw IllegalArgumentException("Unknown ViewModel class")
         }
     }
 }
 
 sealed class EmergencyState {
     object Initial : EmergencyState()
     object Processing : EmergencyState()
     object SafetyConfirmed : EmergencyState()
     data class SOSTriggered(val alert: SOSAlert) : EmergencyState()
     data class IncidentReported(val incident: Incident) : EmergencyState()
     data class LocationShared(val share: LocationShare) : EmergencyState()
     data class ContactAdded(val contact: EmergencyContact) : EmergencyState()
     data class SettingsUpdated(val settings: EmergencySettings) : EmergencyState()
     data class Error(val message: String) : EmergencyState()
 }
