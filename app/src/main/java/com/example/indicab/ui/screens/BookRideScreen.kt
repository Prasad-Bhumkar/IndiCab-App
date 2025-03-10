package com.example.indicab.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.indicab.models.CarType
import com.example.indicab.models.Location
import com.example.indicab.navigation.NavDestination

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookRideScreen(navController: NavController) {
    var pickupLocation by remember { mutableStateOf("") }
    var dropLocation by remember { mutableStateOf("") }
    var selectedCarType by remember { mutableStateOf<CarType?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Book a Ride",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        OutlinedTextField(
            value = pickupLocation,
            onValueChange = { pickupLocation = it },
            label = { Text("Pickup Location") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = dropLocation,
            onValueChange = { dropLocation = it },
            label = { Text("Drop Location") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        )

        Button(
            onClick = {
                // TODO: Implement booking logic
                navController.navigate(NavDestination.TrackRide.route)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = pickupLocation.isNotBlank() && dropLocation.isNotBlank()
        ) {
            Text("Confirm Booking")
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = { navController.navigateUp() },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text("Cancel")
        }
    }
} 