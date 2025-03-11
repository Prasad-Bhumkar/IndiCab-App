package com.example.indicab.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.indicab.utils.AnimationUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnhancedLocationInput(
    pickupLocation: String,
    dropLocation: String,
    onPickupLocationChange: (String) -> Unit,
    onDropLocationChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var isPickupFocused by remember { mutableStateOf(false) }
    var isDropFocused by remember { mutableStateOf(false) }
    var showLocationSuggestions by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize()
    ) {
        // Location Input Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // Pickup Location
                LocationField(
                    value = pickupLocation,
                    onValueChange = onPickupLocationChange,
                    label = "Pickup Location",
                    icon = Icons.Default.LocationOn,
                    isFocused = isPickupFocused,
                    onFocusChange = { focused ->
                        isPickupFocused = focused
                        showLocationSuggestions = focused
                    }
                )

                // Animated Divider with Dots
                LocationConnector()

                // Drop Location
                LocationField(
                    value = dropLocation,
                    onValueChange = onDropLocationChange,
                    label = "Drop Location",
                    icon = Icons.Default.LocationOn,
                    isFocused = isDropFocused,
                    onFocusChange = { focused ->
                        isDropFocused = focused
                        showLocationSuggestions = focused
                    }
                )
            }
        }

        // Location Suggestions
        AnimatedVisibility(
            visible = showLocationSuggestions,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            LocationSuggestions(
                onLocationSelected = { location ->
                    if (isPickupFocused) onPickupLocationChange(location)
                    else if (isDropFocused) onDropLocationChange(location)
                    showLocationSuggestions = false
                    isPickupFocused = false
                    isDropFocused = false
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LocationField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isFocused: Boolean,
    onFocusChange: (Boolean) -> Unit
) {
    val borderColor = if (isFocused) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onFocusChange(true) }
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(24.dp)
                    .then(
                        if (isFocused) AnimationUtils.pulseAnimation(1.1f)
                        else Modifier
                    )
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = value.ifEmpty { "Select location" },
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (value.isEmpty()) {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    },
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun LocationConnector() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        repeat(3) { index ->
            Box(
                modifier = Modifier
                    .size(4.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
            )
            if (index < 2) {
                Spacer(modifier = Modifier.width(4.dp))
            }
        }
    }
}

@Composable
private fun LocationSuggestions(
    onLocationSelected: (String) -> Unit
) {
    val suggestions = listOf(
        "Current Location",
        "Home",
        "Work",
        "Recent Location 1",
        "Recent Location 2"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            suggestions.forEach { suggestion ->
                SuggestionItem(
                    suggestion = suggestion,
                    onSelected = { onLocationSelected(suggestion) }
                )
            }
        }
    }
}

@Composable
private fun SuggestionItem(
    suggestion: String,
    onSelected: () -> Unit
) {
    var isHovered by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSelected),
        color = if (isHovered) {
            MaterialTheme.colorScheme.surfaceVariant
        } else {
            Color.Transparent
        }
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = when (suggestion) {
                    "Current Location" -> Icons.Default.MyLocation
                    "Home" -> Icons.Default.Home
                    "Work" -> Icons.Default.Work
                    else -> Icons.Default.History
                },
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = suggestion,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
