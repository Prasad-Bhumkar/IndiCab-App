package com.example.indicab.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import com.example.indicab.models.DriverPerformance

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DriverPerformanceHistoryScreen(
    performanceHistory: List<DriverPerformance>,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Performance History") },
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
                text = "Performance History Overview",
                style = MaterialTheme.typography.titleLarge
            )

            if (performanceHistory.isEmpty()) {
                Text("No performance history available.")
            } else {
                performanceHistory.forEach { performance ->
                    PerformanceCard(performance = performance)
                }
            }
        }
    }
}

@Composable
private fun PerformanceCard(performance: DriverPerformance) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Performance Period: ${performance.period}",
                style = MaterialTheme.typography.titleMedium
            )
            Text("Acceptance Rate: ${performance.metrics.acceptanceRate}%")
            Text("Cancellation Rate: ${performance.metrics.cancellationRate}%")
            Text("Completion Rate: ${performance.metrics.completionRate}%")
            Text("On-Time Rate: ${performance.metrics.onTimeRate}%")
            Text("Average Response Time: ${performance.metrics.averageResponseTime} seconds")
            Text("Total Rides: ${performance.metrics.totalRides}")
            Text("Total Hours: ${performance.metrics.totalHours} hours")
            Text("Average Rating: ${performance.rating}")
        }
    }
}
