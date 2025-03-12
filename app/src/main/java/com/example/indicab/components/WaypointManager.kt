package com.example.indicab.components

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.indicab.models.Location
import com.example.indicab.models.Waypoint
import com.example.indicab.models.WaypointType
import org.burnoutcrew.reorderable.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WaypointManager(
    waypoints: List<Waypoint>,
    onAddWaypoint: (Location) -> Unit,
    onRemoveWaypoint: (String) -> Unit,
    onReorderWaypoints: (String, Int) -> Unit,
    onWaypointDetailsUpdate: (String, Int?, String?, LocalDateTime?) -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedWaypoint by remember { mutableStateOf<Waypoint?>(null) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Stops",
                style = MaterialTheme.typography.titleMedium
            )
            
            IconButton(
                onClick = { showAddDialog = true }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Stop"
                )
            }
        }

        // Waypoints List
        val state = rememberReorderableLazyListState(onMove = { from, to ->
            val fromWaypoint = waypoints[from.index]
            if (fromWaypoint.type == WaypointType.STOP) {
                onReorderWaypoints(fromWaypoint.id, to.index)
            }
        })

        LazyColumn(
            state = state.listState,
            modifier = Modifier
                .fillMaxWidth()
                .reorderable(state)
        ) {
            itemsIndexed(
                items = waypoints,
                key = { _, waypoint -> waypoint.id }
            ) { index, waypoint ->
                val isDraggable = waypoint.type == WaypointType.STOP
                
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .then(
                            if (isDraggable) {
                                Modifier.detectReorderAfterLongPress(state)
                            } else {
                                Modifier
                            }
                        ),
                    colors = CardDefaults.cardColors(
                        containerColor = when (waypoint.type) {
                            WaypointType.PICKUP -> MaterialTheme.colorScheme.primaryContainer
                            WaypointType.DROPOFF -> MaterialTheme.colorScheme.tertiaryContainer
                            WaypointType.STOP -> MaterialTheme.colorScheme.surfaceVariant
                        }
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Location and Type
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = when (waypoint.type) {
                                    WaypointType.PICKUP -> "Pickup"
                                    WaypointType.DROPOFF -> "Drop-off"
                                    WaypointType.STOP -> "Stop ${index - 1}" // -1 to account for pickup
                                },
                                style = MaterialTheme.typography.labelMedium
                            )
                            
                            Text(
                                text = waypoint.location.address,
                                style = MaterialTheme.typography.bodyMedium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            if (waypoint.notes.isNotEmpty()) {
                                Text(
                                    text = waypoint.notes,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        // Actions
                        Row(
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (isDraggable) {
                                IconButton(
                                    onClick = { selectedWaypoint = waypoint }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Edit Stop"
                                    )
                                }
                                
                                IconButton(
                                    onClick = { onRemoveWaypoint(waypoint.id) }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Remove Stop",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                                
                                Icon(
                                    imageVector = Icons.Default.DragHandle,
                                    contentDescription = "Drag to reorder",
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Add Waypoint Dialog
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Add Stop") },
            text = {
                LocationInput(
                    onLocationSelected = { location ->
                        onAddWaypoint(location)
                        showAddDialog = false
                    }
                )
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Edit Waypoint Dialog
    selectedWaypoint?.let { waypoint ->
        var stopDuration by remember { mutableStateOf(waypoint.stopDuration.toString()) }
        var notes by remember { mutableStateOf(waypoint.notes) }

        AlertDialog(
            onDismissRequest = { selectedWaypoint = null },
            title = { Text("Edit Stop Details") },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedTextField(
                        value = stopDuration,
                        onValueChange = { stopDuration = it },
                        label = { Text("Stop Duration (minutes)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Notes") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onWaypointDetailsUpdate(
                            waypoint.id,
                            stopDuration.toIntOrNull(),
                            notes.takeIf { it.isNotBlank() },
                            null
                        )
                        selectedWaypoint = null
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedWaypoint = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}
