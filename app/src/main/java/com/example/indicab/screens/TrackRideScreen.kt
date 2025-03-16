 package com.example.indicab.ui.screens
 
 import androidx.compose.material3.Text
 import androidx.compose.runtime.Composable
 import androidx.navigation.NavHostController
 
 @Composable
 fun TrackRideScreen(navController: NavHostController) {
     Text(text = "TrackRideScreen")
 }
 
 @Composable
 fun TrackRideScreen(navController: NavController) {
     Column(
         modifier = Modifier
             .fillMaxSize()
             .padding(16.dp)
     ) {
         Text(
             text = "Track Your Ride",
             style = MaterialTheme.typography.headlineMedium,
             modifier = Modifier.padding(bottom = 24.dp)
         )
 
        //  Add map view for ride tracking
         Box(
             modifier = Modifier
                 .fillMaxWidth()
                 .height(300.dp)
                 .padding(bottom = 24.dp)
         ) {
             Text(
                 text = "Map View Coming Soon",
                 modifier = Modifier.align(Alignment.Center)
             )
         }
 
         Card(
             modifier = Modifier
                 .fillMaxWidth()
                 .padding(bottom = 16.dp)
         ) {
             Column(
                 modifier = Modifier
                     .fillMaxWidth()
                     .padding(16.dp)
             ) {
                 Text(
                     text = "Ride Details",
                     style = MaterialTheme.typography.titleMedium,
                     modifier = Modifier.padding(bottom = 8.dp)
                 )
                 Text("Driver: John Doe")
                 Text("Vehicle: KA 01 AB 1234")
                 Text("ETA: 10 minutes")
             }
         }
 
         Button(
             onClick = { navController.navigate(NavDestination.Home.route) {
                 popUpTo(NavDestination.Home.route) { inclusive = true }
             }},
             modifier = Modifier
                 .fillMaxWidth()
                 .height(56.dp)
         ) {
             Text("End Ride")
         }
     }
 }