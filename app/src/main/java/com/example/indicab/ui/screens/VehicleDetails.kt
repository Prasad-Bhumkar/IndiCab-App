package com.example.indicab.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.indicab.models.Vehicle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleDetailsScreen(
    vehicle: Vehicle,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Vehicle Details") },
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
                text = "Vehicle Information",
                style = MaterialTheme.typography.titleLarge
            )

            Text("Make: ${vehicle.make}")
            Text("Model: ${vehicle.model}")
            Text("Year: ${vehicle.year}")
            Text("Color: ${vehicle.color}")
            Text("License Plate: ${vehicle.licensePlate}")
            Text("Type: ${vehicle.type}")

            // Additional vehicle details can be added here
        }
    }
}
