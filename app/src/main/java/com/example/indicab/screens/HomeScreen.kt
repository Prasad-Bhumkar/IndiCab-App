 package com.example.indicab.screens
 
 import androidx.compose.foundation.layout.*
 import androidx.compose.material3.*
 import androidx.compose.runtime.*
 import androidx.compose.ui.Alignment
 import androidx.compose.ui.Modifier
 import androidx.compose.ui.unit.dp
 import androidx.lifecycle.viewmodel.compose.viewModel
 import com.example.indicab.components.PlacesAutocomplete
 import com.example.indicab.viewmodels.HomeViewModel
 import com.google.android.gms.maps.model.CameraPosition
 import com.google.android.gms.maps.model.LatLng
 import com.google.maps.android.compose.*
 
 @OptIn(ExperimentalMaterial3Api::class)
 @Composable
 fun HomeScreen(
     modifier: Modifier = Modifier,
     viewModel: HomeViewModel = viewModel(),
     onNavigateToBookRide: () -> Unit
 ) {
     val uiState by viewModel.uiState.collectAsState()
     val defaultLocation = LatLng(28.7041, 77.1025) // Default to Delhi
     var mapProperties by remember { mutableStateOf(MapProperties(mapType = MapType.NORMAL)) }
     val cameraPositionState = rememberCameraPositionState {
         position = CameraPosition.fromLatLngZoom(defaultLocation, 12f)
     }
 
     Scaffold(
         modifier = modifier.fillMaxSize()
     ) { paddingValues ->
         Column(
             modifier = Modifier
                 .fillMaxSize()
                 .padding(paddingValues)
         ) {
             // Map view taking up top portion
             GoogleMap(
                 modifier = Modifier
                     .fillMaxWidth()
                     .weight(1f),
                 properties = mapProperties,
                 cameraPositionState = cameraPositionState
             ) {
                 // Show markers for pickup and drop locations if available
                 uiState.pickupLocation?.let { location ->
                     Marker(
                         state = MarkerState(position = location),
                         title = "Pickup Location"
                     )
                 }
                 uiState.dropLocation?.let { location ->
                     Marker(
                         state = MarkerState(position = location),
                         title = "Drop Location"
                     )
                 }
             }
 
             // Location selection and booking card
             Surface(
                 modifier = Modifier
                     .fillMaxWidth()
                     .padding(16.dp),
                 shadowElevation = 4.dp,
                 shape = MaterialTheme.shapes.medium
             ) {
                 Column(
                     modifier = Modifier
                         .padding(16.dp)
                         .fillMaxWidth()
                 ) {
                     PlacesAutocomplete(
                         label = "Pickup Location",
                         onPlaceSelected = { latLng ->
                             viewModel.setPickupLocation(latLng)
                             cameraPositionState.position = CameraPosition.fromLatLngZoom(latLng, 15f)
                         }
                     )
 
                     Spacer(modifier = Modifier.height(16.dp))
 
                     PlacesAutocomplete(
                         label = "Drop Location",
                         onPlaceSelected = { latLng ->
                             viewModel.setDropLocation(latLng)
                             cameraPositionState.position = CameraPosition.fromLatLngZoom(latLng, 15f)
                         }
                     )
 
                     Spacer(modifier = Modifier.height(16.dp))
 
                     Button(
                         onClick = onNavigateToBookRide,
                         modifier = Modifier
                             .fillMaxWidth()
                             .height(56.dp),
                         enabled = uiState.pickupLocation != null && uiState.dropLocation != null
                     ) {
                         Text("Book Ride")
                     }
                 }
             }
         }
 
         // Show loading indicator
         if (uiState.isLoading) {
             Box(
                 modifier = Modifier.fillMaxSize(),
                 contentAlignment = Alignment.Center
             ) {
                 CircularProgressIndicator()
             }
         }
 
         // Show error message if any
         uiState.error?.let { error ->
             LaunchedEffect(error) {
                 // Show snackbar or handle error
             }
         }
     }
 } 