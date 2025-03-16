 package com.example.indicab.ui.screens
 
 import androidx.compose.foundation.layout.*
 import androidx.compose.material3.*
 import androidx.compose.runtime.*
 import androidx.compose.ui.Alignment
 import androidx.compose.ui.Modifier
 import androidx.compose.ui.unit.dp
 import androidx.lifecycle.viewmodel.compose.viewModel
 import androidx.navigation.NavController
 import com.example.indicab.models.Driver
 import com.example.indicab.viewmodels.DriverViewModel
 
 @OptIn(ExperimentalMaterial3Api::class)
 @Composable
 fun DriverDashboardScreen(
     navController: NavController,
     driverViewModel: DriverViewModel = viewModel()
 ) {
     val driverState by driverViewModel.driverState.collectAsState()
 
     Scaffold(
         topBar = {
             TopAppBar(
                 title = { Text("Driver Dashboard") },
                 navigationIcon = {
                     IconButton(onClick = { navController.popBackStack() }) {
                         Icon(Icons.Default.ArrowBack, "Back")
                     }
                 }
             )
         }
     ) { padding ->
         Box(
             modifier = Modifier
                 .fillMaxSize()
                 .padding(padding),
             contentAlignment = Alignment.Center
         ) {
             when (driverState) {
                 is DriverState.Loading -> {
                     CircularProgressIndicator()
                 }
                 is DriverState.Error -> {
                     ErrorView(
                         error = (driverState as DriverState.Error).message,
                         onRetry = { driverViewModel.loadDriver() }
                     )
                 }
                 is DriverState.Success -> {
                     DriverDetails((driverState as DriverState.Success).driver)
                 }
             }
         }
     }
 }
 
 @Composable
 private fun DriverDetails(driver: Driver) {
     Column(
         modifier = Modifier
             .fillMaxSize()
             .padding(16.dp),
         verticalArrangement = Arrangement.spacedBy(16.dp)
     ) {
         Text(
             text = "Driver Information",
             style = MaterialTheme.typography.titleLarge
         )
 
         Text("Name: ${driver.firstName} ${driver.lastName}")
         Text("Phone: ${driver.phone}")
         Text("Email: ${driver.email}")
         Text("License Number: ${driver.licenseNumber}")
         Text("Status: ${driver.status}")
 
         // Additional driver details can be added here
     }
 }
 
 @Composable
 private fun ErrorView(
     error: String,
     onRetry: () -> Unit
 ) {
     Column(
         modifier = Modifier
             .fillMaxSize()
             .padding(16.dp),
         horizontalAlignment = Alignment.CenterHorizontally,
         verticalArrangement = Arrangement.Center
     ) {
         Text(
             text = error,
             style = MaterialTheme.typography.bodyLarge,
             color = MaterialTheme.colorScheme.error
         )
         Spacer(modifier = Modifier.height(16.dp))
         Button(onClick = onRetry) {
             Text("Retry")
         }
     }
 }
