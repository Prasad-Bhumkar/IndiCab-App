package com.example.indicab.ui.screens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng

@Composable
fun MapViewScreen() {
    val mapView = remember { MapView(LocalContext.current) }
    mapView.onCreate(null)

    Box(modifier = Modifier.fillMaxSize()) {
        mapView.getMapAsync { googleMap ->
            val currentLocation = LatLng(0.0, 0.0) // Replace with actual location
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15f))
        }

        Text(
            text = "Map View Coming Soon",
            modifier = Modifier.align(Alignment.Center)
        )
    }
}
