package com.example.indicab.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.indicab.models.BookingRequest
import com.example.indicab.viewmodels.ScheduleRideViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleRideScreen(
    bookingRequest: BookingRequest,
    onScheduleComplete: () -> Unit,
    onBackPressed: () -> Unit,
    viewModel: ScheduleRideViewModel = viewModel()
) {
    var selectedDateTime by remember { mutableStateOf<LocalDateTime?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    val scheduleState by viewModel.scheduleState.collectAsState()
    val scheduledRides by viewModel.scheduledRides.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getUpcomingRides()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Schedule Ride") },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = "Select Date and Time",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Button(
                                onClick = { showDatePicker = true },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Select Date")
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = { showTimePicker = true },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Select Time")
                            }
                        }

                        selectedDateTime?.let { dateTime ->
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Selected: ${dateTime.format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm"))}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }

            item {
                Button(
                    onClick = {
                        selectedDateTime?.let { dateTime ->
                            viewModel.scheduleRide(bookingRequest, dateTime)
                            onScheduleComplete()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = selectedDateTime != null
                ) {
                    Text("Confirm Schedule")
                }
            }

            // Upcoming scheduled rides section
            if (scheduledRides.isNotEmpty()) {
                item {
                    Text(
                        text = "Upcoming Rides",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                items(scheduledRides) { ride ->
                    ScheduledRideItem(
                        scheduledRide = ride,
                        onCancel = { viewModel.cancelScheduledRide(ride.id) }
                    )
                }
            }
        }

        // Date Picker Dialog
        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                onDateSelected = { date ->
                    selectedDateTime = date.atTime(
                        selectedDateTime?.toLocalTime() ?: LocalDateTime.now().toLocalTime()
                    )
                    showDatePicker = false
                }
            )
        }

        // Time Picker Dialog
        if (showTimePicker) {
            TimePickerDialog(
                onDismissRequest = { showTimePicker = false },
                onTimeSelected = { time ->
                    selectedDateTime = (selectedDateTime ?: LocalDateTime.now())
                        .withHour(time.hour)
                        .withMinute(time.minute)
                    showTimePicker = false
                }
            )
        }
    }
}

@Composable
fun ScheduledRideItem(
    scheduledRide: ScheduledRide,
    onCancel: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = "Scheduled for: ${scheduledRide.scheduledTime.format(
                    DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm")
                )}",
                style = MaterialTheme.typography.bodyMedium
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "From: ${scheduledRide.bookingRequest.pickupLocation.address}",
                style = MaterialTheme.typography.bodySmall
            )
            
            Text(
                text = "To: ${scheduledRide.bookingRequest.dropLocation.address}",
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Status: ${scheduledRide.status}",
                    style = MaterialTheme.typography.bodyMedium
                )

                if (scheduledRide.status == ScheduleStatus.PENDING) {
                    TextButton(
                        onClick = onCancel,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Cancel")
                    }
                }
            }
        }
    }
}

@Composable
fun DatePickerDialog(
    onDismissRequest: () -> Unit,
    onDateSelected: (LocalDateTime) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Select Date") },
        confirmButton = {
            TextButton(
                onClick = {
                    onDateSelected(LocalDateTime.now().plusDays(1))
                    onDismissRequest()
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun TimePickerDialog(
    onDismissRequest: () -> Unit,
    onTimeSelected: (LocalDateTime) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Select Time") },
        confirmButton = {
            TextButton(
                onClick = {
                    onTimeSelected(LocalDateTime.now().plusHours(1))
                    onDismissRequest()
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Cancel")
            }
        }
    )
}
