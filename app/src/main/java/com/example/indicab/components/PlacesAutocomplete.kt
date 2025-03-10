package com.example.indicab.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Simplified mock data for places
data class PlacePrediction(
    val id: String,
    val primaryText: String,
    val secondaryText: String,
    val latLng: LatLng
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlacesAutocomplete(
    modifier: Modifier = Modifier,
    label: String,
    onPlaceSelected: (LatLng) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var showDropdown by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // Mock data for predictions
    val mockPredictions = listOf(
        PlacePrediction("1", "Airport", "International Airport", LatLng(12.9716, 77.5946)),
        PlacePrediction("2", "City Center", "Downtown", LatLng(12.9716, 77.5946)),
        PlacePrediction("3", "Mall", "Shopping Center", LatLng(12.9716, 77.5946)),
        PlacePrediction("4", "Railway Station", "Central Station", LatLng(12.9716, 77.5946)),
        PlacePrediction("5", "Bus Terminal", "Main Bus Stand", LatLng(12.9716, 77.5946))
    )
    
    var filteredPredictions by remember { mutableStateOf<List<PlacePrediction>>(emptyList()) }

    Column(modifier = modifier) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { query ->
                searchQuery = query
                if (query.length >= 2) {
                    // Filter mock predictions based on query
                    filteredPredictions = mockPredictions.filter { 
                        it.primaryText.contains(query, ignoreCase = true) || 
                        it.secondaryText.contains(query, ignoreCase = true) 
                    }
                    showDropdown = filteredPredictions.isNotEmpty()
                } else {
                    showDropdown = false
                    filteredPredictions = emptyList()
                }
            },
            label = { Text(label) },
            modifier = Modifier.fillMaxWidth()
        )

        if (showDropdown && filteredPredictions.isNotEmpty()) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 200.dp),
                shadowElevation = 4.dp
            ) {
                LazyColumn {
                    items(filteredPredictions) { prediction ->
                        ListItem(
                            headlineContent = { Text(prediction.primaryText) },
                            supportingContent = { Text(prediction.secondaryText) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    searchQuery = prediction.primaryText
                                    onPlaceSelected(prediction.latLng)
                                    showDropdown = false
                                }
                        )
                    }
                }
            }
        }
    }
} 