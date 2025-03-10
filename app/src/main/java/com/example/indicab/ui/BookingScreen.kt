package com.example.indicab.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.indicab.navigation.Screen
import com.example.indicab.components.PlacesAutocomplete
import com.google.android.gms.maps.model.LatLng
import androidx.navigation.compose.rememberNavController
import kotlin.math.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingScreen(navController: NavController) {
    var pickupLocation by remember { mutableStateOf("") }
    var dropoffLocation by remember { mutableStateOf("") }
    var pickupLatLng by remember { mutableStateOf<LatLng?>(null) }
    var dropoffLatLng by remember { mutableStateOf<LatLng?>(null) }
    var time by remember { mutableStateOf("") }
    var selectedVehicle by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        PlacesAutocomplete(
            modifier = Modifier.fillMaxWidth(),
            label = "Pickup Location",
            onPlaceSelected = { latLng ->
                pickupLatLng = latLng
                pickupLocation = "Selected Location"
            }
        )
        
        PlacesAutocomplete(
            modifier = Modifier.fillMaxWidth(),
            label = "Drop-off Location",
            onPlaceSelected = { latLng ->
                dropoffLatLng = latLng
                dropoffLocation = "Selected Location"
            }
        )

        OutlinedTextField(
            value = time,
            onValueChange = { time = it },
            label = { Text("Time") },
            modifier = Modifier.fillMaxWidth()
        )

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = selectedVehicle,
                onValueChange = {},
                readOnly = true,
                label = { Text("Vehicle Type") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Sedan") },
                    onClick = {
                        selectedVehicle = "Sedan"
                        expanded = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("SUV") },
                    onClick = {
                        selectedVehicle = "SUV"
                        expanded = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("Auto-rickshaw") },
                    onClick = {
                        selectedVehicle = "Auto-rickshaw"
                        expanded = false
                    }
                )
            }
        }

        Button(
            onClick = { 
                if (pickupLatLng != null && dropoffLatLng != null) {
                    // Calculate distance using the sphericalDistance extension function
                    val distance = pickupLatLng?.let { pickup ->
                        dropoffLatLng?.let { dropoff ->
                            calculateDistance(pickup, dropoff)
                        }
                    } ?: 0.0
                    
                    val distanceKm = distance / 1000
                    val baseFare = when (selectedVehicle) {
                        "Sedan" -> 50.0 + (distanceKm * 12.0)
                        "SUV" -> 80.0 + (distanceKm * 15.0)
                        "Auto-rickshaw" -> 30.0 + (distanceKm * 8.0)
                        else -> 50.0 + (distanceKm * 12.0)
                    }
                    navController.navigate(Screen.Payment.createRoute(baseFare))
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = pickupLocation.isNotEmpty() && dropoffLocation.isNotEmpty() && selectedVehicle.isNotEmpty()
        ) {
            Text("Book Ride")
        }
    }
}

@Composable
fun PreviewBookingScreen() {
    MaterialTheme {
        BookingScreen(navController = rememberNavController())
    }
}

// Function to calculate distance between two LatLng points using Haversine formula
fun calculateDistance(from: LatLng, to: LatLng): Double {
    val earthRadius = 6371000.0 // meters
    val dLat = Math.toRadians(to.latitude - from.latitude)
    val dLng = Math.toRadians(to.longitude - from.longitude)
    val a = sin(dLat / 2) * sin(dLat / 2) +
            cos(Math.toRadians(from.latitude)) * cos(Math.toRadians(to.latitude)) *
            sin(dLng / 2) * sin(dLng / 2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    return earthRadius * c
}