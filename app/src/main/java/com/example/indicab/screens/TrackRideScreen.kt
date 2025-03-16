<<<<<<< HEAD
 package com.example.indicab.ui.screens
 
 import androidx.compose.material3.Text
 import androidx.compose.runtime.Composable
 import androidx.navigation.NavHostController
 
 @Composable
 fun TrackRideScreen(navController: NavHostController) {
     Text(text = "TrackRideScreen")
 }
 
 @Composable
 fun TrackRideScreen(navController: NavController) {
     Column(
         modifier = Modifier
             .fillMaxSize()
             .padding(16.dp)
     ) {
         Text(
             text = "Track Your Ride",
             style = MaterialTheme.typography.headlineMedium,
             modifier = Modifier.padding(bottom = 24.dp)
         )
 
        //  Add map view for ride tracking
         Box(
             modifier = Modifier
                 .fillMaxWidth()
                 .height(300.dp)
                 .padding(bottom = 24.dp)
         ) {
             Text(
                 text = "Map View Coming Soon",
                 modifier = Modifier.align(Alignment.Center)
             )
         }
 
         Card(
             modifier = Modifier
                 .fillMaxWidth()
                 .padding(bottom = 16.dp)
         ) {
             Column(
                 modifier = Modifier
                     .fillMaxWidth()
                     .padding(16.dp)
             ) {
                 Text(
                     text = "Ride Details",
                     style = MaterialTheme.typography.titleMedium,
                     modifier = Modifier.padding(bottom = 8.dp)
                 )
                 Text("Driver: John Doe")
                 Text("Vehicle: KA 01 AB 1234")
                 Text("ETA: 10 minutes")
             }
         }
 
         Button(
             onClick = { navController.navigate(NavDestination.Home.route) {
                 popUpTo(NavDestination.Home.route) { inclusive = true }
             }},
             modifier = Modifier
                 .fillMaxWidth()
                 .height(56.dp)
         ) {
             Text("End Ride")
         }
     }
 }
=======
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
fun TrackRideScreen(
    onBackPressed: () -> Unit
) {
    val defaultLocation = LatLng(28.7041, 77.1025) // Delhi
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultLocation, 15f)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Track Ride") },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Map view
            GoogleMap(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                cameraPositionState = cameraPositionState
            ) {
                // Driver marker
                Marker(
                    state = MarkerState(position = defaultLocation),
                    title = "Driver Location"
                )
            }

            // Ride details card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Driver is on the way",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Estimated arrival in 10 mins",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { /* Contact driver */ },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Contact Driver")
                    }
                }
            }
        }
    }
}
>>>>>>> 81ec31f166cdb0573d5c5135fcdecb0f6ba49d83
