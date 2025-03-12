package com.example.indicab.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@Composable
fun MapViewScreen(
    currentLocation: LatLng,
    destinationLocation: LatLng? = null,
    onBackPressed: () -> Unit
) {
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(currentLocation, 15f)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(isMyLocationEnabled = true)
        ) {
            // Current location marker
            Marker(
                state = MarkerState(position = currentLocation),
                title = "Current Location"
            )

            // Destination marker if available
            destinationLocation?.let {
                Marker(
                    state = MarkerState(position = it),
                    title = "Destination"
                )
            }
        }

        // Back button
        IconButton(
            onClick = onBackPressed,
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back"
            )
        }
    }
}
