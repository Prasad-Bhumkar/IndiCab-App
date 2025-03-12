package com.example.indicab.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.indicab.models.*
import com.example.indicab.services.DriverService
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

class DriverDashboardViewModel @Inject constructor(
    private val driverService: DriverService,
    private val userId: String
) : ViewModel() {

    private val _dashboardState = MutableStateFlow<DashboardState>(DashboardState.Loading)
    val dashboardState = _dashboardState.asStateFlow()

    private val _earnings = MutableStateFlow<DriverEarnings?>(null)
    val earnings = _earnings.asStateFlow()

    private val _performance = MutableStateFlow<DriverPerformance?>(null)
    val performance = _performance.asStateFlow()

    private val _upcomingRides = MutableStateFlow<List<ScheduledRide>>(emptyList())
    val upcomingRides = _upcomingRides.asStateFlow()

    init {
        loadDashboard()
    }

    private fun loadDashboard() {
        viewModelScope.launch {
            try {
                _dashboardState.value = DashboardState.Loading

                // Load driver data
                driverService.getDriverByUserId(userId).collect { driver ->
                    if (driver != null) {
                        // Load earnings
                        loadEarnings(driver.id)
                        
                        // Load performance metrics
                        loadPerformanceMetrics(driver.id)
                        
                        // Load upcoming rides
                        loadUpcomingRides(driver.id)

                        _dashboardState.value = DashboardState.Success(
                            driver = driver,
                            earnings = _earnings.value,
                            performance = _performance.value,
                            upcomingRides = _upcomingRides.value
                        )
                    } else {
                        _dashboardState.value = DashboardState.Error("Driver not found")
                    }
                }
            } catch (e: Exception) {
                _dashboardState.value = DashboardState.Error(e.message ?: "Failed to load dashboard")
            }
        }
    }

    private suspend fun loadEarnings(driverId: String) {
        // In a real app, this would fetch from a backend service
        // For now, using mock data
        _earnings.value = DriverEarnings(
            driverId = driverId,
            period = EarningsPeriod.DAILY,
            startDate = LocalDateTime.now().minusHours(12),
            endDate = LocalDateTime.now(),
            totalEarnings = 2500.0,
            rideEarnings = 2200.0,
            bonusEarnings = 300.0,
            tips = 150.0,
            totalRides = 12,
            totalHours = 8.5,
            status = EarningsStatus.PENDING
        )
    }

    private suspend fun loadPerformanceMetrics(driverId: String) {
        // In a real app, this would fetch from a backend service
        // For now, using mock data
        _performance.value = DriverPerformance(
            driverId = driverId,
            period = PerformancePeriod.DAILY,
            startDate = LocalDateTime.now().minusHours(12),
            endDate = LocalDateTime.now(),
            metrics = PerformanceMetrics(
                acceptanceRate = 0.95f,
                cancellationRate = 0.02f,
                completionRate = 0.98f,
                onTimeRate = 0.94f,
                averageResponseTime = 45.0,
                averageRating = 4.8f,
                complaints = 0,
                compliments = 5,
                safetyScore = 0.92f,
                totalRides = 12,
                totalHours = 8.5,
                earnings = 2500.0
            ),
            rating = 4.8f,
            status = PerformanceStatus.EXCELLENT
        )
    }

    private suspend fun loadUpcomingRides(driverId: String) {
        // In a real app, this would fetch from a backend service
        // For now, using mock data
        _upcomingRides.value = listOf(
            ScheduledRide(
                id = "RIDE1",
                userId = "USER1",
                driverId = driverId,
                pickup = Location("123 Main St", 12.9716, 77.5946),
                dropoff = Location("456 Park Ave", 12.9716, 77.5946),
                scheduledTime = LocalDateTime.now().plusHours(1),
                status = "SCHEDULED"
            ),
            ScheduledRide(
                id = "RIDE2",
                userId = "USER2",
                driverId = driverId,
                pickup = Location("789 Oak Rd", 12.9716, 77.5946),
                dropoff = Location("321 Pine St", 12.9716, 77.5946),
                scheduledTime = LocalDateTime.now().plusHours(2),
                status = "SCHEDULED"
            )
        )
    }

    fun updateDriverStatus(driver: Driver, newStatus: DriverStatus) {
        viewModelScope.launch {
            try {
                val updatedDriver = driver.copy(
                    status = newStatus,
                    updatedAt = LocalDateTime.now()
                )
                driverService.updateDriver(updatedDriver)
                
                // Refresh dashboard after status update
                loadDashboard()
            } catch (e: Exception) {
                _dashboardState.value = DashboardState.Error("Failed to update status: ${e.message}")
            }
        }
    }

    fun startBreak(driver: Driver) {
        updateDriverStatus(driver, DriverStatus.ON_BREAK)
    }

    fun endBreak(driver: Driver) {
        updateDriverStatus(driver, DriverStatus.AVAILABLE)
    }

    fun refreshDashboard() {
        loadDashboard()
    }

    class Factory @Inject constructor(
        private val driverService: DriverService,
        private val userId: String
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(DriverDashboardViewModel::class.java)) {
                return DriverDashboardViewModel(driverService, userId) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

sealed class DashboardState {
    object Loading : DashboardState()
    data class Success(
        val driver: Driver,
        val earnings: DriverEarnings?,
        val performance: DriverPerformance?,
        val upcomingRides: List<ScheduledRide>
    ) : DashboardState()
    data class Error(val message: String) : DashboardState()
}
