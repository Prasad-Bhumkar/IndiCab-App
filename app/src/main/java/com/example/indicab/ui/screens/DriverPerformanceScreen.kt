package com.example.indicab.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.indicab.models.DriverPerformance

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DriverPerformanceScreen(
    performance: DriverPerformance,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Driver Performance") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Performance Metrics",
                style = MaterialTheme.typography.titleLarge
            )

            Text("Acceptance Rate: ${performance.metrics.acceptanceRate}%")
            Text("Cancellation Rate: ${performance.metrics.cancellationRate}%")
            Text("Completion Rate: ${performance.metrics.completionRate}%")
            Text("On-Time Rate: ${performance.metrics.onTimeRate}%")
            Text("Average Response Time: ${performance.metrics.averageResponseTime} seconds")
            Text("Total Rides: ${performance.metrics.totalRides}")
            Text("Total Hours: ${performance.metrics.totalHours} hours")
            Text("Average Rating: ${performance.rating}")

            // Additional performance metrics can be added here
        }
    }
}
