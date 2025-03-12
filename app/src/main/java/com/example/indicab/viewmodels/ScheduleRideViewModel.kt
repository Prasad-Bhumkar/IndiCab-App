package com.example.indicab.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.indicab.models.BookingRequest
import com.example.indicab.models.ScheduledRide
import com.example.indicab.models.ScheduleStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class ScheduleRideViewModel : ViewModel() {
    private val _scheduleState = MutableStateFlow<ScheduleState>(ScheduleState.Initial)
    val scheduleState: StateFlow<ScheduleState> = _scheduleState

    private val _scheduledRides = MutableStateFlow<List<ScheduledRide>>(emptyList())
    val scheduledRides: StateFlow<List<ScheduledRide>> = _scheduledRides

    fun scheduleRide(bookingRequest: BookingRequest, scheduledTime: LocalDateTime) {
        viewModelScope.launch {
            _scheduleState.value = ScheduleState.Processing
            try {
                // TODO: Integrate with actual API
                val scheduledRide = ScheduledRide(
                    id = generateScheduleId(),
                    userId = "current_user_id", // TODO: Get from UserManager
                    bookingRequest = bookingRequest,
                    scheduledTime = scheduledTime
                )
                
                // Simulate API call
                simulateScheduling()
                
                // Update state with success
                _scheduleState.value = ScheduleState.Success(scheduledRide)
                
                // Update scheduled rides list
                val currentList = _scheduledRides.value.toMutableList()
                currentList.add(scheduledRide)
                _scheduledRides.value = currentList
            } catch (e: Exception) {
                _scheduleState.value = ScheduleState.Error(e.message ?: "Failed to schedule ride")
            }
        }
    }

    fun cancelScheduledRide(rideId: String) {
        viewModelScope.launch {
            try {
                // TODO: Integrate with actual API
                val updatedList = _scheduledRides.value.map { ride ->
                    if (ride.id == rideId) {
                        ride.copy(status = ScheduleStatus.CANCELLED)
                    } else {
                        ride
                    }
                }
                _scheduledRides.value = updatedList
            } catch (e: Exception) {
                _scheduleState.value = ScheduleState.Error("Failed to cancel ride")
            }
        }
    }

    fun getUpcomingRides() {
        viewModelScope.launch {
            try {
                // TODO: Integrate with actual API
                // For now, filter existing rides
                val upcomingRides = _scheduledRides.value.filter { 
                    it.scheduledTime.isAfter(LocalDateTime.now()) &&
                    it.status == ScheduleStatus.PENDING
                }
                _scheduledRides.value = upcomingRides
            } catch (e: Exception) {
                _scheduleState.value = ScheduleState.Error("Failed to fetch upcoming rides")
            }
        }
    }

    private suspend fun simulateScheduling() {
        kotlinx.coroutines.delay(1000) // Simulate network delay
    }

    private fun generateScheduleId(): String {
        return "SCH" + System.currentTimeMillis()
    }
}

sealed class ScheduleState {
    object Initial : ScheduleState()
    object Processing : ScheduleState()
    data class Success(val scheduledRide: ScheduledRide) : ScheduleState()
    data class Error(val message: String) : ScheduleState()
}
