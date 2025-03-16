 package com.example.indicab.ui
 
 import androidx.compose.foundation.layout.*
 import androidx.compose.material3.MaterialTheme
 import androidx.compose.material3.Surface
 import androidx.compose.material3.Text
 import androidx.compose.runtime.Composable
 import androidx.compose.ui.Alignment
 import androidx.compose.ui.Modifier
 import androidx.compose.ui.unit.dp
 import com.google.android.gms.maps.model.CameraPosition
 import com.google.android.gms.maps.model.LatLng
 import com.google.maps.android.compose.*
 
 @Composable
 fun RideTrackingScreen() {
     val currentLocation = LatLng(40.7128, -74.0060) // Example driver location
     val pickupLocation = LatLng(40.7208, -73.9846)  // Example pickup location
     
     val cameraPositionState = rememberCameraPositionState {
         position = CameraPosition.fromLatLngZoom(currentLocation, DEFAULT_ZOOM)
     }
 
     Box(modifier = Modifier.fillMaxSize()) {
         GoogleMap(
             modifier = Modifier.fillMaxSize(),
             cameraPositionState = cameraPositionState
         ) {
             Marker(
                 state = MarkerState(position = currentLocation),
                 title = "Driver's Current Location"
             )
             Marker(
                 state = MarkerState(position = pickupLocation),
                 title = "Pickup Location"
             )
         }
 
         // ETA Card
         Surface(
             modifier = Modifier
                 .align(Alignment.TopCenter)
                 .padding(16.dp),
             color = MaterialTheme.colorScheme.surface,
             shadowElevation = 4.dp
         ) {
             Column(
                 modifier = Modifier.padding(16.dp),
                 horizontalAlignment = Alignment.CenterHorizontally
             ) {
                 Text(
                     text = "Estimated Time of Arrival",
                     style = MaterialTheme.typography.titleMedium
                 )
                 Spacer(modifier = Modifier.height(4.dp))
                 Text(
                     text = "5 minutes",
                     style = MaterialTheme.typography.headlineSmall
                 )
             }
         }
     }
 }
 
 private const val DEFAULT_ZOOM = 15f 