package com.example.indicab.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class SimpleCarType(
    val name: String,
    val description: String,
    val price: Double
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleBookingScreen(
    onConfirmBooking: () -> Unit
) {
    var selectedCarType by remember { mutableStateOf<SimpleCarType?>(null) }

    val carTypes = listOf(
        SimpleCarType("Mini", "Swift, WagonR", 299.0),
        SimpleCarType("Sedan", "Dzire, Etios", 399.0),
        SimpleCarType("SUV", "Innova, Ertiga", 599.0)
    )

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Header
            TopAppBar(
                title = { Text("Select Car Type") }
            )

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp)
            ) {
                items(carTypes) { carType ->
                    CarTypeCard(
                        carType = carType,
                        isSelected = carType == selectedCarType,
                        onSelect = { selectedCarType = carType }
                    )
                }
            }

            // Bottom Bar with Confirm Button
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    selectedCarType?.let { carType ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total Fare")
                            Text(
                                "₹${carType.price}",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Button(
                        onClick = onConfirmBooking,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        enabled = selectedCarType != null
                    ) {
                        Text("Confirm Booking")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CarTypeCard(
    carType: SimpleCarType,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        onClick = onSelect,
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) 
                MaterialTheme.colorScheme.primaryContainer 
            else 
                MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = carType.name,
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = carType.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "₹${carType.price}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}
