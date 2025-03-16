package com.example.indicab.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleHomeScreen(
    onBookRide: () -> Unit
) {
    var pickupLocation by remember { mutableStateOf("") }
    var dropLocation by remember { mutableStateOf("") }
    
    val defaultLocation = LatLng(28.7041, 77.1025) // Delhi
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultLocation, 12f)
    }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Map View
            GoogleMap(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                cameraPositionState = cameraPositionState
            )

            // Booking Card
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shadowElevation = 4.dp,
                shape = MaterialTheme.shapes.medium
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    // Pickup Location
                    OutlinedTextField(
                        value = pickupLocation,
                        onValueChange = { pickupLocation = it },
                        label = { Text("Pickup Location") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Drop Location
                    OutlinedTextField(
                        value = dropLocation,
                        onValueChange = { dropLocation = it },
                        label = { Text("Drop Location") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Book Button
                    Button(
                        onClick = onBookRide,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        enabled = pickupLocation.isNotEmpty() && dropLocation.isNotEmpty()
                    ) {
                        Text("Book Ride")
                    }
                }
            }
        }
    }
}
