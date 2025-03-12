package com.example.indicab.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.indicab.models.SOSType
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SOSButton(
    onTrigger: (SOSType, String?) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }
    var isLongPressing by remember { mutableStateOf(false) }
    var pressProgress by remember { mutableStateOf(0f) }
    val coroutineScope = rememberCoroutineScope()

    val animatedColor by animateColorAsState(
        targetValue = if (isLongPressing) {
            Color.Red.copy(alpha = 0.8f)
        } else {
            MaterialTheme.colorScheme.error
        }
    )

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        // Pulsating background
        if (isLongPressing) {
            val infiniteTransition = rememberInfiniteTransition()
            val scale by infiniteTransition.animateFloat(
                initialValue = 1f,
                targetValue = 1.2f,
                animationSpec = infiniteRepeatable(
                    animation = tween(500),
                    repeatMode = RepeatMode.Reverse
                )
            )

            Box(
                modifier = Modifier
                    .size(80.dp)
                    .scale(scale)
                    .clip(CircleShape)
                    .background(animatedColor.copy(alpha = 0.2f))
            )
        }

        // Main SOS button
        Button(
            onClick = { showDialog = true },
            modifier = Modifier
                .size(72.dp)
                .border(
                    width = 2.dp,
                    color = animatedColor,
                    shape = CircleShape
                ),
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = animatedColor
            ),
            contentPadding = PaddingValues(0.dp)
        ) {
            if (isLongPressing) {
                CircularProgressIndicator(
                    progress = pressProgress,
                    modifier = Modifier.size(32.dp),
                    color = MaterialTheme.colorScheme.onError
                )
            } else {
                Text(
                    text = "SOS",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onError
                )
            }
        }
    }

    // Emergency type selection dialog
    if (showDialog) {
        EmergencyTypeDialog(
            onDismiss = { showDialog = false },
            onTypeSelected = { type, description ->
                onTrigger(type, description)
                showDialog = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EmergencyTypeDialog(
    onDismiss: () -> Unit,
    onTypeSelected: (SOSType, String?) -> Unit
) {
    var selectedType by remember { mutableStateOf<SOSType?>(null) }
    var description by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Emergency Type",
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Emergency type selection
                EmergencyTypeSelector(
                    selectedType = selectedType,
                    onTypeSelected = { selectedType = it }
                )

                // Description input
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Description (Optional)") },
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    selectedType?.let { type ->
                        onTypeSelected(
                            type,
                            description.takeIf { it.isNotBlank() }
                        )
                    }
                },
                enabled = selectedType != null
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun EmergencyTypeSelector(
    selectedType: SOSType?,
    onTypeSelected: (SOSType) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SOSType.values().forEach { type ->
            EmergencyTypeOption(
                type = type,
                isSelected = type == selectedType,
                onSelect = { onTypeSelected(type) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EmergencyTypeOption(
    type: SOSType,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Card(
        onClick = onSelect,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = when (type) {
                    SOSType.MEDICAL -> Icons.Default.LocalHospital
                    SOSType.SECURITY -> Icons.Default.Security
                    SOSType.ACCIDENT -> Icons.Default.Warning
                    SOSType.BREAKDOWN -> Icons.Default.Build
                    SOSType.OTHER -> Icons.Default.Help
                },
                contentDescription = null
            )
            
            Column {
                Text(
                    text = type.name,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = when (type) {
                        SOSType.MEDICAL -> "Medical emergency"
                        SOSType.SECURITY -> "Security threat"
                        SOSType.ACCIDENT -> "Vehicle accident"
                        SOSType.BREAKDOWN -> "Vehicle breakdown"
                        SOSType.OTHER -> "Other emergency"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
