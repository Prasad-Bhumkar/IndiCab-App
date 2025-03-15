package com.example.indicab.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.indicab.components.*
import com.example.indicab.models.Location
import com.example.indicab.navigation.NavDestination
import com.example.indicab.utils.AnimationUtils
import java.time.LocalDate
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnhancedBookRideScreen(
    navController: NavController,
    onMenuClick: () -> Unit,
    context: Context
) {
    var selectedTripType by remember { mutableStateOf(TripType.ONEWAY) }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var selectedTime by remember { mutableStateOf(LocalTime.now()) }
    var selectedCarType by remember { mutableStateOf<Int?>(null) }
    var showFareDetails by remember { mutableStateOf(false) }
    val waypointManager = remember { WaypointManager() }
    val favoriteLocationRepository = remember {
        FavoriteLocationRepository(
            AppDatabase.getInstance(context).favoriteLocationDao()
        )
    }

    // Car types data with images
    data class CarTypeData(
        val type: String,
        val description: String,
        val capacity: Pair<Int, Int>,
        val imageUrl: String,
        val originalPrice: Double,
        val discountedPrice: Double
    )

    val carTypes = listOf(
        CarTypeData(
            type = "Mini",
            description = "Swift, WagonR",
            capacity = Pair(4, 1),
            imageUrl = "https://raw.githubusercontent.com/Prasad-Bhumkar/IndiCab/main/app/src/main/res/drawable/car_mini.png",
            originalPrice = 6499.0,
            discountedPrice = 5590.0
        ),
        CarTypeData(
            type = "Prime",
            description = "Spacious car",
            capacity = Pair(4, 2),
            imageUrl = "https://raw.githubusercontent.com/Prasad-Bhumkar/IndiCab/main/app/src/main/res/drawable/car_prime.png",
            originalPrice = 6992.0,
            discountedPrice = 6080.0
        ),
        CarTypeData(
            type = "XL SUV",
            description = "Solid XL SUV",
            capacity = Pair(6, 3),
            imageUrl = "https://raw.githubusercontent.com/Prasad-Bhumkar/IndiCab/main/app/src/main/res/drawable/car_xl.png",
            originalPrice = 7950.0,
            discountedPrice = 6920.0
        )
    )

    val scrollState = rememberScrollState()
    var isBookingEnabled by remember {
        mutableStateOf(false)
    }

    // Update booking button state
    LaunchedEffect(
        waypointManager.pickupLocation,
        waypointManager.dropLocation,
        selectedCarType
    ) {
        isBookingEnabled = waypointManager.pickupLocation != null &&
                          waypointManager.dropLocation != null &&
                          selectedCarType != null
    }

    Scaffold(
        topBar = {
            AppTopBar(
                onMenuClick = onMenuClick
            )
        },
        bottomBar = {
            AnimatedVisibility(
                visible = isBookingEnabled,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp,
                    shadowElevation = 8.dp
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "SERVICE OF LOYAL INDIAN DRIVERS",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        
                        Button(
                            onClick = {
                                if (selectedCarType != null) {
                                    showFareDetails = true
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .then(AnimationUtils.buttonPressAnimation(isBookingEnabled)),
                            enabled = isBookingEnabled
                        ) {
                            Text("Book Now")
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
        ) {
            // Trip Type Selector with Animation
            AnimatedContent(
                targetState = selectedTripType,
                transitionSpec = {
                    fadeIn(animationSpec = tween(300)) with
                    fadeOut(animationSpec = tween(300))
                }
            ) { tripType ->
                EnhancedTripTypeSelector(
                    selectedType = tripType,
                    onTypeSelected = { selectedTripType = it },
                    modifier = Modifier.padding(16.dp)
                )
            }

            // Location Inputs
            EnhancedLocationInput(
                waypointManager = waypointManager,
                onLocationSelected = { /* Handle location selection */ },
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            // Date Time Picker
            EnhancedDateTimePicker(
                selectedDate = selectedDate,
                selectedTime = selectedTime,
                onDateSelected = { selectedDate = it },
                onTimeSelected = { selectedTime = it },
                modifier = Modifier.padding(16.dp)
            )

            // Car Type Cards with Animation
            carTypes.forEachIndexed { index, carData ->
                AnimatedVisibility(
                    visible = true,
                    enter = slideInHorizontally(
                        initialOffsetX = { it },
                        animationSpec = tween(
                            durationMillis = 300,
                            delayMillis = index * 100
                        )
                    ) + fadeIn(
                        animationSpec = tween(300)
                    )
                ) {
                    EnhancedCarTypeCard(
                        carType = carData.type,
                        description = carData.description,
                        seats = carData.capacity.first,
                        bags = carData.capacity.second,
                        originalPrice = carData.originalPrice,
                        discountedPrice = carData.discountedPrice,
                        imageUrl = carData.imageUrl,
                        onSelect = { selectedCarType = index },
                        isSelected = selectedCarType == index,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }
        }

        // Fare Details Modal
        if (showFareDetails && selectedCarType != null) {
            val selectedCar = carTypes[selectedCarType!!]
            FareDetailsModal(
                carType = selectedCar.type,
                distance = calculateDistance(waypointManager.getRoute()),
                estimatedFare = calculateFare(selectedCar.discountedPrice, waypointManager.getRoute()),
                onDismiss = { showFareDetails = false },
                onBook = {
                    showFareDetails = false
                    navController.navigate(
                        NavDestination.Payment.createRoute(selectedCar.discountedPrice)
                    )
                }
            )
        }
    }
}

private fun calculateDistance(route: List<Location>): Double {
    return LocationUtils.calculateRouteDistance(route)
}

private fun calculateFare(baseFare: Double, route: List<Location>): Double {
    val distance = LocationUtils.calculateRouteDistance(route)
    val waypointsCount = max(0, route.size - 2) // Exclude pickup and drop locations
    return LocationUtils.calculateFare(baseFare, distance, waypointsCount)
}
