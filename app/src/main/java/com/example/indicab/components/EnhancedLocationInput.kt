 package com.example.indicab.components
 
 import androidx.compose.animation.*
 import androidx.compose.animation.core.*
 import androidx.compose.foundation.*
 import androidx.compose.foundation.layout.*
 import androidx.compose.foundation.shape.*
 import androidx.compose.material.icons.Icons
 import androidx.compose.material.icons.filled.*
 import androidx.compose.material3.*
 import androidx.compose.runtime.*
 import androidx.compose.ui.*
 import androidx.compose.ui.graphics.Color
 import androidx.compose.ui.text.style.TextOverflow
 import androidx.compose.ui.unit.dp
 import com.example.indicab.models.Location
 import com.example.indicab.utils.AnimationUtils
 
 @OptIn(ExperimentalMaterial3Api::class)
 @Composable
 fun EnhancedLocationInput(
     waypointManager: WaypointManager,
     favoriteLocationRepository: FavoriteLocationRepository,
     onLocationSelected: (Location) -> Unit,
     modifier: Modifier = Modifier
 ) {
     val favoriteLocations by favoriteLocationRepository.getFavoriteLocations("currentUser").collectAsState(initial = emptyList())
     var showLocationSuggestions by remember { mutableStateOf(false) }
     var focusedField by remember { mutableStateOf<FocusedField?>(null) }
 
     Column(
         modifier = modifier
             .fillMaxWidth()
             .animateContentSize()
     ) {
         // Location Input Card
         Card(
             modifier = Modifier
                 .fillMaxWidth()
                 .padding(vertical = 8.dp),
             shape = RoundedCornerShape(16.dp),
             elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
         ) {
             Column(
                 modifier = Modifier.padding(16.dp)
             ) {
                 // Pickup Location
                 LocationField(
                     value = waypointManager.pickupLocation,
                     label = "Pickup Location",
                     icon = Icons.Default.LocationOn,
                     isFocused = focusedField == FocusedField.Pickup,
                     onFocusChange = { focused ->
                         focusedField = if (focused) FocusedField.Pickup else null
                         showLocationSuggestions = focused
                     }
                 )
 
                 // Waypoints
                 waypointManager.waypoints.forEachIndexed { index, location ->
                     AnimatedVisibility(
                         visible = true,
                         enter = slideInVertically() + fadeIn(),
                         exit = slideOutVertically() + fadeOut()
                     ) {
                         Column {
                             LocationConnector()
                             WaypointField(
                                 location = location,
                                 index = index,
                                 onEdit = {
                                     focusedField = FocusedField.Waypoint(index)
                                     showLocationSuggestions = true
                                 },
                                 onRemove = { waypointManager.removeWaypoint(location) }
                             )
                         }
                     }
                 }
 
                 // Add Waypoint Button
                 if (waypointManager.waypoints.size < 3) {
                     AddWaypointButton(
                         onClick = {
                             focusedField = FocusedField.Waypoint(waypointManager.waypoints.size)
                             showLocationSuggestions = true
                         }
                     )
                 }
 
                 // Drop Location
                 LocationField(
                     value = waypointManager.dropLocation,
                     label = "Drop Location",
                     icon = Icons.Default.LocationOn,
                     isFocused = focusedField == FocusedField.Drop,
                     onFocusChange = { focused ->
                         focusedField = if (focused) FocusedField.Drop else null
                         showLocationSuggestions = focused
                     }
                 )
             }
         }
 
         // Location Suggestions
         AnimatedVisibility(
             visible = showLocationSuggestions,
             enter = fadeIn() + expandVertically(),
             exit = fadeOut() + shrinkVertically()
         ) {
             LocationSuggestions(
                 favoriteLocations = favoriteLocations,
                 onLocationSelected = { location ->
                     when (focusedField) {
                         is FocusedField.Pickup -> waypointManager.setPickupLocation(location)
                         is FocusedField.Drop -> waypointManager.setDropLocation(location)
                         is FocusedField.Waypoint -> {
                             val index = (focusedField as FocusedField.Waypoint).index
                             waypointManager.updateWaypoint(index, location)
                         }
                         null -> {}
                     }
                     onLocationSelected(location)
                     showLocationSuggestions = false
                     focusedField = null
                 },
                 onFavoriteToggle = { favoriteLocation ->
                     if (favoriteLocations.any { it.name == favoriteLocation.name }) {
                         favoriteLocationRepository.delete(favoriteLocation.id)
                     } else {
                         favoriteLocationRepository.insert(favoriteLocation)
                     }
                 }
             )
         }
     }
 }
 
 @Composable
 private fun AddWaypointButton(onClick: () -> Unit) {
     Surface(
         modifier = Modifier
             .fillMaxWidth()
             .clickable(onClick = onClick)
             .padding(vertical = 8.dp),
         shape = RoundedCornerShape(12.dp),
         color = MaterialTheme.colorScheme.surfaceVariant
     ) {
         Row(
             modifier = Modifier
             .padding(12.dp),
             verticalAlignment = Alignment.CenterVertically
         ) {
             Icon(
                 imageVector = Icons.Default.Add,
                 contentDescription = "Add waypoint",
                 tint = MaterialTheme.colorScheme.primary
             )
             Spacer(modifier = Modifier.width(8.dp))
             Text(
                 text = "Add stop",
                 style = MaterialTheme.typography.bodyLarge,
                 color = MaterialTheme.colorScheme.primary
             )
         }
     }
 }
 
 @Composable
 private fun WaypointField(
     location: Location,
     index: Int,
     onEdit: () -> Unit,
     onRemove: () -> Unit
 ) {
     Surface(
         modifier = Modifier
             .fillMaxWidth()
             .clickable(onClick = onEdit),
         shape = RoundedCornerShape(12.dp),
         color = MaterialTheme.colorScheme.surfaceVariant
     ) {
         Row(
             modifier = Modifier
                 .padding(12.dp),
             verticalAlignment = Alignment.CenterVertically
         ) {
             Text(
                 text = "Stop ${index + 1}",
                 style = MaterialTheme.typography.bodySmall,
                 color = MaterialTheme.colorScheme.onSurfaceVariant
             )
             Spacer(modifier = Modifier.width(8.dp))
             Text(
                 text = location.name,
                 style = MaterialTheme.typography.bodyLarge,
                 color = MaterialTheme.colorScheme.onSurface,
                 maxLines = 1,
                 overflow = TextOverflow.Ellipsis
             )
             Spacer(modifier = Modifier.weight(1f))
             IconButton(onClick = onRemove) {
                 Icon(
                     imageVector = Icons.Default.Close,
                     contentDescription = "Remove waypoint",
                     tint = MaterialTheme.colorScheme.error
                 )
             }
         }
     }
 }
 
 private sealed class FocusedField {
     object Pickup : FocusedField()
     object Drop : FocusedField()
     data class Waypoint(val index: Int) : FocusedField()
 }
