package com.example.indicab.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.indicab.utils.AnimationUtils
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnhancedDateTimePicker(
    selectedDate: LocalDate,
    selectedTime: LocalTime,
    onDateSelected: (LocalDate) -> Unit,
    onTimeSelected: (LocalTime) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var isDateHovered by remember { mutableStateOf(false) }
    var isTimeHovered by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = "Select Date & Time",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Date Selector
            Card(
                modifier = Modifier
                    .weight(1f)
                    .then(
                        if (isDateHovered)
                            AnimationUtils.cardSelectionModifier(true)
                        else
                            Modifier
                    )
                    .clickable {
                        showDatePicker = true
                        isDateHovered = true
                    },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isDateHovered)
                        MaterialTheme.colorScheme.primaryContainer
                    else
                        MaterialTheme.colorScheme.surface
                )
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = "Select Date",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "Date",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = selectedDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy")),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Time Selector
            Card(
                modifier = Modifier
                    .weight(1f)
                    .then(
                        if (isTimeHovered)
                            AnimationUtils.cardSelectionModifier(true)
                        else
                            Modifier
                    )
                    .clickable {
                        showTimePicker = true
                        isTimeHovered = true
                    },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isTimeHovered)
                        MaterialTheme.colorScheme.primaryContainer
                    else
                        MaterialTheme.colorScheme.surface
                )
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = "Select Time",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "Time",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = selectedTime.format(DateTimeFormatter.ofPattern("hh:mm a")),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // Date Picker Dialog
        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = {
                    showDatePicker = false
                    isDateHovered = false
                },
                confirmButton = {
                    TextButton(onClick = {
                        showDatePicker = false
                        isDateHovered = false
                    }) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showDatePicker = false
                        isDateHovered = false
                    }) {
                        Text("Cancel")
                    }
                }
            ) {
                DatePicker(
                    state = rememberDatePickerState(
                        initialSelectedDateMillis = selectedDate
                            .atStartOfDay()
                            .toInstant(java.time.ZoneOffset.UTC)
                            .toEpochMilli()
                    ),
                    showModeToggle = false,
                    title = { Text("Select Date") }
                )
            }
        }

        // Time Picker Dialog
        if (showTimePicker) {
            TimePickerDialog(
                onDismissRequest = {
                    showTimePicker = false
                    isTimeHovered = false
                },
                confirmButton = {
                    TextButton(onClick = {
                        showTimePicker = false
                        isTimeHovered = false
                    }) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showTimePicker = false
                        isTimeHovered = false
                    }) {
                        Text("Cancel")
                    }
                }
            ) {
                TimePicker(
                    state = rememberTimePickerState(
                        initialHour = selectedTime.hour,
                        initialMinute = selectedTime.minute
                    ),
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}