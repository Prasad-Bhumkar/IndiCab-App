 package com.example.indicab.ui.screens
 
 import androidx.compose.foundation.layout.*
 import androidx.compose.material3.*
 import androidx.compose.runtime.*
 import androidx.compose.ui.Alignment
 import androidx.compose.ui.unit.dp
 import com.example.indicab.models.DriverEarnings
 
 @OptIn(ExperimentalMaterial3Api::class)
 @Composable
 fun DriverEarningsScreen(
     earnings: DriverEarnings,
     onBack: () -> Unit
 ) {
     Scaffold(
         topBar = {
             TopAppBar(
                 title = { Text("Driver Earnings") },
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
                 text = "Earnings Overview",
                 style = MaterialTheme.typography.titleLarge
             )
 
             Text("Total Earnings: $${earnings.totalEarnings}")
             Text("Ride Earnings: $${earnings.rideEarnings}")
             Text("Bonus Earnings: $${earnings.bonusEarnings}")
             Text("Tips: $${earnings.tips}")
             Text("Total Rides: ${earnings.totalRides}")
             Text("Period: ${earnings.period}")
 
             // Additional earnings details can be added here
         }
     }
 }
