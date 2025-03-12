package com.example.indicab.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.indicab.components.*
import com.example.indicab.models.*
import com.example.indicab.navigation.NavDestinations
import com.example.indicab.utils.AnimationUtils
import com.example.indicab.viewmodels.WaypointViewModel
import com.google.gson.Gson
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookRideScreen(
    navController: NavController,
    onMenuClick: () -> Unit,
    waypointViewModel: WaypointViewModel = viewModel()
) {
    var selectedTripType by remember { mutableStateOf(TripType.ONEWAY) }
    var selectedCarType by remember { mutableStateOf<Int?>(null) }
    var showFareDetails by remember { mutableStateOf(false) }
    var showBookingOptions by remember { mutableStateOf(false) }

    val waypoints by waypointViewModel.waypoints.collectAsState()
    val routeState by waypointViewModel.routeState.collectAsState()

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
    LaunchedEffect(waypoints, selectedCarType) {
        isBookingEnabled = waypoints.size >= 2 && selectedCarType != null
    }

    // Initialize pickup and dropoff if waypoints is empty
    LaunchedEffect(Unit) {
        if (waypoints.isEmpty()) {
            waypointViewModel.addWaypoint(
                Location(address = ""),
                WaypointType.PICKUP
            )
            waypointViewModel.addWaypoint(
                Location(address = ""),
                WaypointType.DROPOFF
            )
        }
    }

    fun createBookingRequest(): BookingRequest {
        val route = (routeState as? RouteState.Success)?.route
            ?: RouteWithWaypoints(waypoints = waypoints)
            
        return BookingRequest.fromRoute(
            route = route,
            carTypeId = selectedCarType?.toString() ?: "",
            carType = selectedCarType?.let { carTypes[it].first } ?: "",
            tripType = selectedTripType.name
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Book Ride") },
                navigationIcon = {
                    IconButton(onClick = onMenuClick) {
                        Icon(Icons.Default.Menu, "Menu")
                    }
                }
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
                            onClick = { showBookingOptions = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .then(AnimationUtils.buttonPressAnimation(isBookingEnabled)),
                            enabled = isBookingEnabled
                        ) {
                            Text("Continue")
                        }
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
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
                TripTypeSelector(
                    selectedType = tripType,
                    onTypeSelected = { selectedTripType = it },
                    modifier = Modifier.padding(16.dp)
                )
            }

            // Waypoint Manager
            WaypointManager(
                waypoints = waypoints,
                onAddWaypoint = { location ->
                    waypointViewModel.addWaypoint(location, WaypointType.STOP)
                },
                onRemoveWaypoint = { waypointId ->
                    waypointViewModel.removeWaypoint(waypointId)
                },
                onReorderWaypoints = { waypointId, newOrder ->
                    waypointViewModel.updateWaypointOrder(waypointId, newOrder)
                },
                onWaypointDetailsUpdate = { waypointId, duration, notes, scheduledArrival ->
                    waypointViewModel.updateWaypointDetails(
                        waypointId = waypointId,
                        stopDuration = duration,
                        notes = notes,
                        scheduledArrival = scheduledArrival
                    )
                },
                modifier = Modifier.padding(vertical = 16.dp)
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
                    CarTypeCard(
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

        // Booking Options Dialog
        if (showBookingOptions) {
            AlertDialog(
                onDismissRequest = { showBookingOptions = false },
                title = { Text("Choose Booking Type") },
                text = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Button(
                            onClick = {
                                showBookingOptions = false
                                showFareDetails = true
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Book Now")
                        }
                        
                        Button(
                            onClick = {
                                showBookingOptions = false
                                val bookingRequest = createBookingRequest()
                                val bookingRequestJson = Gson().toJson(bookingRequest)
                                val encodedJson = URLEncoder.encode(bookingRequestJson, StandardCharsets.UTF_8.toString())
                                navController.navigate(NavDestinations.ScheduleRide.createRoute(encodedJson))
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Schedule for Later")
                        }
                    }
                },
                confirmButton = {},
                dismissButton = {
                    TextButton(onClick = { showBookingOptions = false }) {
                        Text("Cancel")
                    }
                }
            )
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
                    navController.navigate(NavDestinations.Payment.route)
                }
            )
        }
    }
}

enum class TripType {
    ONEWAY,
    ROUND
}
