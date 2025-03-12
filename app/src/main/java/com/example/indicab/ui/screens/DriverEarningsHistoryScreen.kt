package com.example.indicab.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import com.example.indicab.models.DriverEarnings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DriverEarningsHistoryScreen(
    earningsHistory: List<DriverEarnings>,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Earnings History") },
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
                text = "Earnings History Overview",
                style = MaterialTheme.typography.titleLarge
            )

            if (earningsHistory.isEmpty()) {
                Text("No earnings history available.")
            } else {
                earningsHistory.forEach { earnings ->
                    EarningsCard(earnings = earnings)
                }
            }
        }
    }
}

@Composable
private fun EarningsCard(earnings: DriverEarnings) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Earnings Period: ${earnings.period}",
                style = MaterialTheme.typography.titleMedium
            )
            Text("Total Earnings: $${earnings.totalEarnings}")
            Text("Ride Earnings: $${earnings.rideEarnings}")
            Text("Bonus Earnings: $${earnings.bonusEarnings}")
            Text("Tips: $${earnings.tips}")
            Text("Total Rides: ${earnings.totalRides}")
            Text("Total Hours: ${earnings.totalHours} hours")
        }
    }
}
