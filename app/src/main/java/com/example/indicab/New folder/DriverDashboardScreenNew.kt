package com.example.indicab.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.indicab.components.*
import com.example.indicab.models.*
import com.example.indicab.viewmodels.DriverViewModel
import java.time.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DriverDashboardScreen(
    navController: NavController,
    driverViewModel: DriverViewModel = viewModel()
) {
    val driverState by driverViewModel.driverState.collectAsState()
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Driver Dashboard") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Handle notifications */ }) {
                        Icon(Icons.Default.Notifications, "Notifications")
                    }
                }
            )
        }
    ) { padding ->
        when (driverState) {
            is DriverState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {
                    CircularProgressIndicator()
                }
            }
            is DriverState.Error -> {
                ErrorView(
                    error = (driverState as DriverState.Error).message,
                    onRetry = { driverViewModel.loadDriver() }
                )
            }
            is DriverState.Success -> {
                val driver = (driverState as DriverState.Success).driver
                
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(scrollState),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Status Toggle
                    StatusToggleCard(
                        currentStatus = driver.status,
                        onStatusChange = { newStatus ->
                            driverViewModel.updateDriver(
                                driver.copy(status = newStatus)
                            )
                        }
                    )
                    
                    // Earnings Summary
                    val currentEarnings = DriverEarnings(
                        driverId = driver.id,
                        period = EarningsPeriod.DAILY,
                        startDate = LocalDateTime.now().minusHours(12),
                        endDate = LocalDateTime.now(),
                        totalEarnings = driver.totalEarnings,
                        totalRides = driver.totalRides,
                        totalHours = 8.5 // This should come from actual tracking
                    )
                    EarningsSummaryCard(earnings = currentEarnings)
                    
                    // Performance Metrics
                    val performance = DriverPerformance(
                        driverId = driver.id,
                        period = PerformancePeriod.DAILY,
                        startDate = LocalDateTime.now().minusHours(12),
                        endDate = LocalDateTime.now(),
                        metrics = PerformanceMetrics(
                            acceptanceRate = 0.95f,
                            completionRate = 0.98f,
                            averageRating = driver.rating,
                            safetyScore = 0.92f
                        )
                    )
                    PerformanceMetricsCard(performance = performance)
                    
                    // Break Management
                    BreakManagementCard(
                        preferences = driver.preferences.breakPreferences,
                        onBreakStart = {
                            driverViewModel.updateDriver(
                                driver.copy(status = DriverStatus.ON_BREAK)
                            )
                        }
                    )
                    
                    // Upcoming Rides
                    val upcomingRides = listOf(
                        ScheduledRide(
                            id = "RIDE1",
                            userId = "USER1",
                            driverId = driver.id,
                            pickup = Location("123 Main St", 12.9716, 77.5946),
                            dropoff = Location("456 Park Ave", 12.9716, 77.5946),
                            scheduledTime = LocalDateTime.now().plusHours(1),
                            status = "SCHEDULED"
                        ),
                        ScheduledRide(
                            id = "RIDE2",
                            userId = "USER2",
                            driverId = driver.id,
                            pickup = Location("789 Oak Rd", 12.9716, 77.5946),
                            dropoff = Location("321 Pine St", 12.9716, 77.5946),
                            scheduledTime = LocalDateTime.now().plusHours(2),
                            status = "SCHEDULED"
                        )
                    )
                    RideQueueCard(upcomingRides = upcomingRides)
                    
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
            else -> {
                // Handle other states if needed
            }
        }
    }
}

@Composable
private fun ErrorView(
    error: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = error,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) {
            Text("Retry")
        }
    }
}
