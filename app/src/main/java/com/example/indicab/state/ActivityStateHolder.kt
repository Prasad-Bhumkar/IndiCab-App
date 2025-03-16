package com.example.indicab.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class ActivityStateHolder {
    var hasLocationPermission by mutableStateOf(false)
        private set

    fun updateLocationPermissionState(granted: Boolean) {
        hasLocationPermission = granted
    }

    var isLocationPermissionRationaleShown by mutableStateOf(false)
        private set

    fun showLocationPermissionRationale() {
        isLocationPermissionRationaleShown = true
    }

    var activityState by mutableStateOf(ActivityState.IDLE)
        private set

    fun updateActivityState(newState: ActivityState) {
        activityState = newState
    }
}

enum class ActivityState {
    IDLE,
    PERMISSION_REQUESTED,
    PERMISSION_GRANTED,
    PERMISSION_DENIED,
    ERROR
}
