package com.example.indicab.ui.screens

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
import com.example.indicab.navigation.NavDestination
import com.example.indicab.utils.AnimationUtils
import java.time.LocalDate
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnhancedBookRideScreen(
    navController: NavController,
    onMenuClick: () -> Unit
) {
    var selectedTripType by remember { mutableStateOf(TripType.ONEWAY) }
    var pickupLocation by remember { mutableStateOf("") }
    var dropLocation by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var selectedTime by remember { mutableStateOf(LocalTime.now()) }
    var selectedCarType by remember { mutableStateOf<Int?>(null) }
    var showFareDetails by remember { mutableStateOf(false) }

    val carTypes = listOf(
        Triple("Mini", "Swift, WagonR", Pair(4, 1)),
        Triple("Prime", "Spacious car", Pair(4, 2)),
        Triple("XL SUV", "Solid XL SUV", Pair(6, 3))
    )

    val prices = listOf(
        Pair(6499.0, 5590.0),
        Pair(6992.0, 6080.0),
        Pair(7950.0, 6920.0)
    )

    val scrollState = rememberScrollState()
    var isBookingEnabled by remember {
        mutableStateOf(false)
    }

    // Update booking button state
    LaunchedEffect(pickupLocation, dropLocation, selectedCarType) {
        isBookingEnabled = pickupLocation.isNotBlank() && 
                          dropLocation.isNotBlank() && 
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
                pickupLocation = pickupLocation,
                dropLocation = dropLocation,
                onPickupLocationChange = { pickupLocation = it },
                onDropLocationChange = { dropLocation = it },
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
            carTypes.forEachIndexed { index, (type, description, capacity) ->
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
                        carType = type,
                        description = description,
                        seats = capacity.first,
                        bags = capacity.second,
                        originalPrice = prices[index].first,
                        discountedPrice = prices[index].second,
                        onSelect = { selectedCarType = index },
                        isSelected = selectedCarType == index,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }
        }

        // Fare Details Modal
        if (showFareDetails && selectedCarType != null) {
            FareDetailsModal(
                carType = carTypes[selectedCarType!!].first,
                distance = 350,
                estimatedFare = prices[selectedCarType!!].second,
                onDismiss = { showFareDetails = false },
                onBook = {
                    showFareDetails = false
                    navController.navigate(
                        NavDestinationWithArguments.Payment.createRoute(
                            prices[selectedCarType!!].second
                        )
                    )
                }
            )
        }
    }
}
