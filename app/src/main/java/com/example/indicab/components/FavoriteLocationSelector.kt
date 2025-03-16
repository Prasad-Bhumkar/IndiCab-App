 package com.example.indicab.components
 
 import androidx.compose.animation.*
 import androidx.compose.foundation.clickable
 import androidx.compose.foundation.layout.*
 import androidx.compose.foundation.lazy.LazyColumn
 import androidx.compose.foundation.lazy.LazyRow
 import androidx.compose.foundation.lazy.items
 import androidx.compose.material.icons.Icons
 import androidx.compose.material.icons.filled.*
 import androidx.compose.material3.*
 import androidx.compose.runtime.*
 import androidx.compose.ui.Alignment
 import androidx.compose.ui.Modifier
 import androidx.compose.ui.text.style.TextOverflow
 import androidx.compose.ui.unit.dp
 import com.example.indicab.models.FavoriteLocation
 import com.example.indicab.models.FavoriteLocationType
 import com.example.indicab.models.Location
 
 @OptIn(ExperimentalMaterial3Api::class)
 @Composable
 fun FavoriteLocationSelector(
     favoriteLocations: List<FavoriteLocation>,
     recentLocations: List<FavoriteLocation>,
     homeAndWorkLocations: List<FavoriteLocation>,
     onLocationSelected: (Location) -> Unit,
     onAddToFavorites: (Location, String, FavoriteLocationType, List<String>) -> Unit,
     modifier: Modifier = Modifier
 ) {
     var showAddDialog by remember { mutableStateOf(false) }
     var selectedLocation by remember { mutableStateOf<Location?>(null) }
     var searchQuery by remember { mutableStateOf("") }
 
     Column(
         modifier = modifier.fillMaxWidth()
     ) {
         // Search Bar
         OutlinedTextField(
             value = searchQuery,
             onValueChange = { searchQuery = it },
             modifier = Modifier
                 .fillMaxWidth()
                 .padding(bottom = 16.dp),
             placeholder = { Text("Search saved locations") },
             leadingIcon = {
                 Icon(Icons.Default.Search, "Search")
             },
             trailingIcon = {
                 if (searchQuery.isNotEmpty()) {
                     IconButton(onClick = { searchQuery = "" }) {
                         Icon(Icons.Default.Clear, "Clear search")
                     }
                 }
             }
         )
 
         // Home and Work Locations
         if (homeAndWorkLocations.isNotEmpty()) {
             Text(
                 text = "Home & Work",
                 style = MaterialTheme.typography.titleMedium,
                 modifier = Modifier.padding(bottom = 8.dp)
             )
             LazyRow(
                 horizontalArrangement = Arrangement.spacedBy(8.dp),
                 modifier = Modifier.padding(bottom = 16.dp)
             ) {
                 items(homeAndWorkLocations) { location ->
                     HomeWorkLocationCard(
                         location = location,
                         onClick = { onLocationSelected(location.location) }
                     )
                 }
             }
         }
 
         // Recent Locations
         if (recentLocations.isNotEmpty()) {
             Text(
                 text = "Recent Places",
                 style = MaterialTheme.typography.titleMedium,
                 modifier = Modifier.padding(bottom = 8.dp)
             )
             LazyRow(
                 horizontalArrangement = Arrangement.spacedBy(8.dp),
                 modifier = Modifier.padding(bottom = 16.dp)
             ) {
                 items(recentLocations) { location ->
                     RecentLocationCard(
                         location = location,
                         onClick = { onLocationSelected(location.location) }
                     )
                 }
             }
         }
 
         // All Favorite Locations
         Text(
             text = "Saved Places",
             style = MaterialTheme.typography.titleMedium,
             modifier = Modifier.padding(bottom = 8.dp)
         )
         LazyColumn(
             verticalArrangement = Arrangement.spacedBy(8.dp)
         ) {
             items(
                 favoriteLocations.filter {
                     it.label.contains(searchQuery, ignoreCase = true) ||
                     it.tags.any { tag -> tag.contains(searchQuery, ignoreCase = true) }
                 }
             ) { location ->
                 FavoriteLocationCard(
                     location = location,
                     onClick = { onLocationSelected(location.location) }
                 )
             }
         }
     }
 
     // Add to Favorites Dialog
     if (showAddDialog && selectedLocation != null) {
         AddToFavoritesDialog(
             location = selectedLocation!!,
             onDismiss = { showAddDialog = false },
             onSave = { label, type, tags ->
                 onAddToFavorites(selectedLocation!!, label, type, tags)
                 showAddDialog = false
             }
         )
     }
 }
 
 @OptIn(ExperimentalMaterial3Api::class)
 @Composable
 private fun HomeWorkLocationCard(
     location: FavoriteLocation,
     onClick: () -> Unit
 ) {
     Card(
         modifier = Modifier
             .width(160.dp)
             .clickable(onClick = onClick)
     ) {
         Column(
             modifier = Modifier.padding(12.dp)
         ) {
             Icon(
                 imageVector = when (location.type) {
                     FavoriteLocationType.HOME -> Icons.Default.Home
                     FavoriteLocationType.WORK -> Icons.Default.Work
                     else -> Icons.Default.LocationOn
                 },
                 contentDescription = location.type.name
             )
             Spacer(modifier = Modifier.height(8.dp))
             Text(
                 text = location.label,
                 style = MaterialTheme.typography.titleSmall
             )
             Text(
                 text = location.location.address,
                 style = MaterialTheme.typography.bodySmall,
                 maxLines = 1,
                 overflow = TextOverflow.Ellipsis
             )
         }
     }
 }
 
 @OptIn(ExperimentalMaterial3Api::class)
 @Composable
 private fun RecentLocationCard(
     location: FavoriteLocation,
     onClick: () -> Unit
 ) {
     Card(
         modifier = Modifier
             .width(160.dp)
             .clickable(onClick = onClick)
     ) {
         Column(
             modifier = Modifier.padding(12.dp)
         ) {
             Icon(Icons.Default.History, "Recent")
             Spacer(modifier = Modifier.height(8.dp))
             Text(
                 text = location.label,
                 style = MaterialTheme.typography.titleSmall
             )
             Text(
                 text = location.location.address,
                 style = MaterialTheme.typography.bodySmall,
                 maxLines = 1,
                 overflow = TextOverflow.Ellipsis
             )
         }
     }
 }
 
 @OptIn(ExperimentalMaterial3Api::class)
 @Composable
 private fun FavoriteLocationCard(
     location: FavoriteLocation,
     onClick: () -> Unit
 ) {
     Card(
         modifier = Modifier
             .fillMaxWidth()
             .clickable(onClick = onClick)
     ) {
         Row(
             modifier = Modifier.padding(16.dp),
             verticalAlignment = Alignment.CenterVertically
         ) {
             Icon(
                 imageVector = when (location.type) {
                     FavoriteLocationType.HOME -> Icons.Default.Home
                     FavoriteLocationType.WORK -> Icons.Default.Work
                     FavoriteLocationType.FAMILY -> Icons.Default.People
                     FavoriteLocationType.FRIEND -> Icons.Default.Person
                     FavoriteLocationType.OTHER -> Icons.Default.LocationOn
                 },
                 contentDescription = location.type.name
             )
             Spacer(modifier = Modifier.width(16.dp))
             Column(modifier = Modifier.weight(1f)) {
                 Text(
                     text = location.label,
                     style = MaterialTheme.typography.titleSmall
                 )
                 Text(
                     text = location.location.address,
                     style = MaterialTheme.typography.bodySmall,
                     maxLines = 1,
                     overflow = TextOverflow.Ellipsis
                 )
                 if (location.tags.isNotEmpty()) {
                     Text(
                         text = location.tags.joinToString(", "),
                         style = MaterialTheme.typography.bodySmall,
                         color = MaterialTheme.colorScheme.onSurfaceVariant
                     )
                 }
             }
         }
     }
 }
 
 @Composable
 private fun AddToFavoritesDialog(
     location: Location,
     onDismiss: () -> Unit,
     onSave: (String, FavoriteLocationType, List<String>) -> Unit
 ) {
     var label by remember { mutableStateOf("") }
     var selectedType by remember { mutableStateOf(FavoriteLocationType.OTHER) }
     var tags by remember { mutableStateOf("") }
 
     AlertDialog(
         onDismissRequest = onDismiss,
         title = { Text("Save Location") },
         text = {
             Column(
                 modifier = Modifier.fillMaxWidth(),
                 verticalArrangement = Arrangement.spacedBy(16.dp)
             ) {
                 OutlinedTextField(
                     value = label,
                     onValueChange = { label = it },
                     label = { Text("Label") },
                     modifier = Modifier.fillMaxWidth()
                 )
 
                 OutlinedTextField(
                     value = tags,
                     onValueChange = { tags = it },
                     label = { Text("Tags (comma separated)") },
                     modifier = Modifier.fillMaxWidth()
                 )
 
                 // Type Selection
                 Column {
                     Text("Location Type", style = MaterialTheme.typography.labelMedium)
                     FavoriteLocationType.values().forEach { type ->
                         Row(
                             modifier = Modifier
                                 .fillMaxWidth()
                                 .clickable { selectedType = type }
                                 .padding(vertical = 8.dp),
                             verticalAlignment = Alignment.CenterVertically
                         ) {
                             RadioButton(
                                 selected = selectedType == type,
                                 onClick = { selectedType = type }
                             )
                             Spacer(modifier = Modifier.width(8.dp))
                             Text(type.name)
                         }
                     }
                 }
             }
         },
         confirmButton = {
             TextButton(
                 onClick = {
                     onSave(
                         label,
                         selectedType,
                         tags.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                     )
                 },
                 enabled = label.isNotBlank()
             ) {
                 Text("Save")
             }
         },
         dismissButton = {
             TextButton(onClick = onDismiss) {
                 Text("Cancel")
             }
         }
     )
 }
