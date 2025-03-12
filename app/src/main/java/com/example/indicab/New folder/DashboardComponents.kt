package com.example.indicab.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.indicab.models.*
import java.time.format.DateTimeFormatter

@Composable
fun StatusToggleCard(
    currentStatus: DriverStatus,
    onStatusChange: (DriverStatus) -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Driver Status",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            DriverStatus.values().forEach { status ->
                RadioButton(
                    selected = status == currentStatus,
                    onClick = { onStatusChange(status) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = status.name.replace("_", " ").capitalize(),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
fun EarningsSummaryCard(
    earnings: DriverEarnings
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Earnings Summary",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Total Earnings")
                    Text(
                        text = "₹${earnings.totalEarnings}",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Column {
                    Text("Total Rides")
                    Text(
                        text = "${earnings.totalRides}",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }
            
            LinearProgressIndicator(
                progress = (earnings.totalHours / 12f).coerceIn(0f, 1f),
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = "Hours Worked: ${earnings.totalHours}h / 12h",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun PerformanceMetricsCard(
    performance: DriverPerformance
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Performance Metrics",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            MetricRow("Acceptance Rate", performance.metrics.acceptanceRate)
            MetricRow("Completion Rate", performance.metrics.completionRate)
            MetricRow("Rating", performance.metrics.averageRating)
            MetricRow("Safety Score", performance.metrics.safetyScore)
        }
    }
}

@Composable
private fun MetricRow(label: String, value: Float) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label)
        Text(
            text = "${(value * 100).toInt()}%",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun BreakManagementCard(
    preferences: BreakPreferences,
    onBreakStart: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Break Management",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Text("Break Duration: ${preferences.breakDuration} minutes")
            Text("Next Break: ${preferences.preferredBreakTime}")
            
            Button(
                onClick = onBreakStart,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Start Break")
            }
        }
    }
}

@Composable
fun RideQueueCard(
    upcomingRides: List<ScheduledRide>
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Upcoming Rides",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            if (upcomingRides.isEmpty()) {
                Text(
                    text = "No upcoming rides",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                LazyColumn {
                    items(upcomingRides) { ride ->
                        RideItem(ride)
                    }
                }
            }
        }
    }
}

@Composable
private fun RideItem(ride: ScheduledRide) {
    val formatter = DateTimeFormatter.ofPattern("HH:mm")
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = ride.pickup.address,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "→ ${ride.dropoff.address}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            text = formatter.format(ride.scheduledTime),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
