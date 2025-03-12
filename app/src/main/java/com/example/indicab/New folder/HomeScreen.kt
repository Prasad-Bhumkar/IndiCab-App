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
                    Box(modifier = Modifier.fillMaxWidth()) {
                        PlacesAutocomplete(
                            label = "Pickup Location",
                            value = uiState.pickupQuery,
                            onValueChange = viewModel::updatePickupQuery,
                            onPlaceSelected = { prediction ->
                                viewModel.setPickupLocation(
                                    location = prediction.latLng,
                                    address = prediction.primaryText
                                )
                                viewModel.hideLocationSuggestions()
                                cameraPositionState.position = CameraPosition.fromLatLngZoom(prediction.latLng, 15f)
                            },
                            showSuggestions = uiState.showLocationSuggestions && uiState.pickupQuery.length >= 2
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Box(modifier = Modifier.fillMaxWidth()) {
                        PlacesAutocomplete(
                            label = "Drop Location",
                            value = uiState.dropQuery,
                            onValueChange = viewModel::updateDropQuery,
                            onPlaceSelected = { prediction ->
                                viewModel.setDropLocation(
                                    location = prediction.latLng,
                                    address = prediction.primaryText
                                )
                                viewModel.hideLocationSuggestions()
                                cameraPositionState.position = CameraPosition.fromLatLngZoom(prediction.latLng, 15f)
                            },
                            showSuggestions = uiState.showLocationSuggestions && uiState.dropQuery.length >= 2
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Show available car types if both locations are set
                    if (uiState.pickupLocation != null && uiState.dropLocation != null) {
                        CarTypesList(
                            carTypes = uiState.availableCarTypes,
                            selectedCarType = uiState.selectedCarType,
                            onCarTypeSelected = viewModel::selectCarType
                        )
                    }

                    // Show fare details if available
                    uiState.fareDetails?.let { fareDetails ->
                        Spacer(modifier = Modifier.height(16.dp))
                        FareDetailsCard(fareDetails = fareDetails)
                    }

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