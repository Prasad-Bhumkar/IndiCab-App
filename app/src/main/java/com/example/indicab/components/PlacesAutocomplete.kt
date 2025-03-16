<<<<<<< HEAD
 package com.example.indicab.components
 
 import androidx.compose.animation.*
 import androidx.compose.foundation.background
 import androidx.compose.foundation.clickable
 import androidx.compose.foundation.layout.*
 import androidx.compose.foundation.lazy.LazyColumn
 import androidx.compose.foundation.lazy.items
 import androidx.compose.foundation.shape.RoundedCornerShape
 import androidx.compose.material.icons.Icons
 import androidx.compose.material.icons.filled.*
 import androidx.compose.material3.*
 import androidx.compose.runtime.*
 import androidx.compose.ui.Alignment
 import androidx.compose.ui.Modifier
 import androidx.compose.ui.draw.clip
 import androidx.compose.ui.graphics.Color
 import androidx.compose.ui.text.font.FontWeight
 import androidx.compose.ui.unit.dp
 import com.google.android.gms.maps.model.LatLng
 
 data class PlacePrediction(
     val id: String,
     val primaryText: String,
     val secondaryText: String,
     val latLng: LatLng,
     val type: PlaceType = PlaceType.REGULAR
 )
 
 enum class PlaceType {
     CURRENT_LOCATION,
     HOME,
     WORK,
     RECENT,
     REGULAR
 }
 
 @OptIn(ExperimentalMaterial3Api::class)
 @Composable
 fun PlacesAutocomplete(
     modifier: Modifier = Modifier,
     label: String,
     value: String,
     onValueChange: (String) -> Unit,
     onPlaceSelected: (PlacePrediction) -> Unit
 ) {
     var showDropdown by remember { mutableStateOf(false) }
     var filteredPredictions by remember { mutableStateOf<List<PlacePrediction>>(emptyList()) }
 
     // Mock data for saved and recent locations
     val savedLocations = listOf(
         PlacePrediction("current", "Current Location", "Using GPS", LatLng(0.0, 0.0), PlaceType.CURRENT_LOCATION),
         PlacePrediction("home", "Home", "123 Home Street", LatLng(12.9716, 77.5946), PlaceType.HOME),
         PlacePrediction("work", "Work", "456 Office Complex", LatLng(12.9716, 77.5946), PlaceType.WORK)
     )
 
     val recentLocations = listOf(
         PlacePrediction("recent1", "City Mall", "Shopping District", LatLng(12.9716, 77.5946), PlaceType.RECENT),
         PlacePrediction("recent2", "Central Station", "Railway Station", LatLng(12.9716, 77.5946), PlaceType.RECENT)
     )
 
     Column(modifier = modifier) {
         // Search TextField
         OutlinedTextField(
             value = value,
             onValueChange = { query ->
                 onValueChange(query)
                 if (query.length >= 2) {
                     // Filter predictions based on query
                     filteredPredictions = if (query.isEmpty()) {
                         savedLocations + recentLocations
                     } else {
                         (savedLocations + recentLocations).filter { 
                             it.primaryText.contains(query, ignoreCase = true) || 
                             it.secondaryText.contains(query, ignoreCase = true) 
                         }
                     }
                     showDropdown = true
                 } else {
                     filteredPredictions = savedLocations + recentLocations
                     showDropdown = query.isEmpty()
                 }
             },
             label = { Text(label) },
             leadingIcon = {
                 Icon(
                     imageVector = Icons.Default.LocationOn,
                     contentDescription = null,
                     tint = MaterialTheme.colorScheme.primary
                 )
             },
             trailingIcon = if (value.isNotEmpty()) {
                 {
                     IconButton(onClick = { 
                         onValueChange("")
                         showDropdown = true
                         filteredPredictions = savedLocations + recentLocations
                     }) {
                         Icon(
                             imageVector = Icons.Default.Clear,
                             contentDescription = "Clear"
                         )
                     }
                 }
             } else null,
             modifier = Modifier
                 .fillMaxWidth()
                 .clip(RoundedCornerShape(12.dp)),
             colors = OutlinedTextFieldDefaults.colors(
                 focusedBorderColor = MaterialTheme.colorScheme.primary,
                 unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
             )
         )
 
         AnimatedVisibility(
             visible = showDropdown,
             enter = fadeIn() + expandVertically(),
             exit = fadeOut() + shrinkVertically()
         ) {
             Surface(
                 modifier = Modifier
                     .fillMaxWidth()
                     .padding(top = 8.dp)
                     .clip(RoundedCornerShape(12.dp)),
                 tonalElevation = 2.dp
             ) {
                 LazyColumn(
                     modifier = Modifier.heightIn(max = 300.dp)
                 ) {
                     items(filteredPredictions) { prediction ->
                         PlacePredictionItem(
                             prediction = prediction,
                             onClick = {
                                 onPlaceSelected(prediction)
                                 showDropdown = false
                             }
                         )
                     }
                 }
             }
         }
     }
 }
 
 @Composable
 private fun PlacePredictionItem(
     prediction: PlacePrediction,
     onClick: () -> Unit
 ) {
     ListItem(
         headlineContent = { 
             Text(
                 text = prediction.primaryText,
                 style = MaterialTheme.typography.bodyLarge,
                 fontWeight = FontWeight.Medium
             )
         },
         supportingContent = {
             Text(
                 text = prediction.secondaryText,
                 style = MaterialTheme.typography.bodyMedium,
                 color = MaterialTheme.colorScheme.onSurfaceVariant
             )
         },
         leadingContent = {
             Icon(
                 imageVector = when (prediction.type) {
                     PlaceType.CURRENT_LOCATION -> Icons.Default.MyLocation
                     PlaceType.HOME -> Icons.Default.Home
                     PlaceType.WORK -> Icons.Default.Work
                     PlaceType.RECENT -> Icons.Default.History
                     PlaceType.REGULAR -> Icons.Default.LocationOn
                 },
                 contentDescription = null,
                 tint = MaterialTheme.colorScheme.primary
             )
         },
         modifier = Modifier
             .fillMaxWidth()
             .clickable(onClick = onClick)
             .padding(horizontal = 8.dp)
     )
 }
=======
package com.example.indicab.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.LatLng

data class PlacePrediction(
    val id: String,
    val primaryText: String,
    val secondaryText: String,
    val latLng: LatLng,
    val type: PlaceType = PlaceType.REGULAR
)

enum class PlaceType {
    CURRENT_LOCATION,
    HOME,
    WORK,
    RECENT,
    REGULAR
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlacesAutocomplete(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    onPlaceSelected: (PlacePrediction) -> Unit,
    showSuggestions: Boolean = false
) {
    var filteredPredictions by remember { mutableStateOf<List<PlacePrediction>>(emptyList()) }
    
    val locations = remember {
        listOf(
            PlacePrediction("current", "Current Location", "Using GPS", LatLng(0.0, 0.0), PlaceType.CURRENT_LOCATION),
            PlacePrediction("home", "Home", "123 Home Street", LatLng(12.9716, 77.5946), PlaceType.HOME),
            PlacePrediction("work", "Work", "456 Office Complex", LatLng(12.9716, 77.5946), PlaceType.WORK),
            PlacePrediction("recent1", "City Mall", "Shopping District", LatLng(12.9716, 77.5946), PlaceType.RECENT),
            PlacePrediction("recent2", "Central Station", "Railway Station", LatLng(12.9716, 77.5946), PlaceType.RECENT)
        )
    }

    Column(modifier = modifier) {
        // Search TextField
        OutlinedTextField(
            value = value,
            onValueChange = { query ->
                onValueChange(query)
                filteredPredictions = if (query.length >= 2) {
                    locations.filter { 
                        it.primaryText.contains(query, ignoreCase = true) || 
                        it.secondaryText.contains(query, ignoreCase = true) 
                    }
                } else {
                    locations
                }
            },
            label = { Text(label) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            trailingIcon = if (value.isNotEmpty()) {
                {
                    IconButton(onClick = { 
                        onValueChange("")
                        filteredPredictions = locations
                    }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear"
                        )
                    }
                }
            } else null,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp)),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
            )
        )

        if (showSuggestions) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .animateContentSize(),
                tonalElevation = 2.dp
            ) {
                LazyColumn(
                    modifier = Modifier.heightIn(max = 300.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(
                        items = filteredPredictions,
                        key = { it.id }
                    ) { prediction ->
                        PlacePredictionItem(
                            prediction = prediction,
                            onClick = {
                                onPlaceSelected(prediction)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PlacePredictionItem(
    prediction: PlacePrediction,
    onClick: () -> Unit
) {
    ListItem(
        headlineContent = { 
            Text(
                text = prediction.primaryText,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
        },
        supportingContent = {
            Text(
                text = prediction.secondaryText,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        leadingContent = {
            Icon(
                imageVector = when (prediction.type) {
                    PlaceType.CURRENT_LOCATION -> Icons.Default.MyLocation
                    PlaceType.HOME -> Icons.Default.Home
                    PlaceType.WORK -> Icons.Default.Work
                    PlaceType.RECENT -> Icons.Default.History
                    PlaceType.REGULAR -> Icons.Default.LocationOn
                },
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp)
    )
}
>>>>>>> 81ec31f166cdb0573d5c5135fcdecb0f6ba49d83
