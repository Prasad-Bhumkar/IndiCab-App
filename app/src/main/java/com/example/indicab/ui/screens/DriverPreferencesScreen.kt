 package com.example.indicab.ui.screens
 
 import androidx.compose.foundation.layout.*
 import androidx.compose.material3.*
 import androidx.compose.runtime.*
 import androidx.compose.ui.Alignment
 import androidx.compose.ui.unit.dp
 import com.example.indicab.models.DriverPreferences
 
 @OptIn(ExperimentalMaterial3Api::class)
 @Composable
 fun DriverPreferencesScreen(
     preferences: DriverPreferences,
     onBack: () -> Unit,
     onUpdate: (DriverPreferences) -> Unit
 ) {
     Scaffold(
         topBar = {
             TopAppBar(
                 title = { Text("Driver Preferences") },
                 navigationIcon = {
                     IconButton(onClick = onBack) {
                         Icon(Icons.Default.ArrowBack, "Back")
                     }
                 }
             )
         }
     ) { padding ->
         Column(
             modifier = Modifier
                 .fillMaxSize()
                 .padding(padding)
                 .padding(16.dp),
             verticalArrangement = Arrangement.spacedBy(16.dp)
         ) {
             Text(
                 text = "Preferences Overview",
                 style = MaterialTheme.typography.titleLarge
             )
 
             // Max Distance Preference
             OutlinedTextField(
                 value = preferences.maxDistance.toString(),
                 onValueChange = { newValue ->
                     val newDistance = newValue.toDoubleOrNull() ?: preferences.maxDistance
                     onUpdate(preferences.copy(maxDistance = newDistance))
                 },
                 label = { Text("Max Distance (km)") },
                 modifier = Modifier.fillMaxWidth()
             )
 
             // Preferred Areas Preference
             OutlinedTextField(
                 value = preferences.preferredAreas.joinToString(", "),
                 onValueChange = { newValue ->
                     val newAreas = newValue.split(",").map { it.trim() }
                     onUpdate(preferences.copy(preferredAreas = newAreas))
                 },
                 label = { Text("Preferred Areas") },
                 modifier = Modifier.fillMaxWidth()
             )
 
             // Working Hours Preference
             Text("Working Hours", style = MaterialTheme.typography.titleMedium)
             // Add UI elements for working hours settings here
 
             // Break Preferences
             Text("Break Preferences", style = MaterialTheme.typography.titleMedium)
             // Add UI elements for break preferences here
 
             // Notifications Preferences
             Text("Notification Preferences", style = MaterialTheme.typography.titleMedium)
             // Add UI elements for notification preferences here
         }
     }
 }
