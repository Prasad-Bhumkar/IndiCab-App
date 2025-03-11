package com.example.indicab.components

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Luggage
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.indicab.utils.AnimationUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnhancedCarTypeCard(
    carType: String,
    description: String,
    seats: Int,
    bags: Int,
    originalPrice: Double,
    discountedPrice: Double,
    onSelect: () -> Unit,
    isSelected: Boolean,
    modifier: Modifier = Modifier
) {
    var showDetails by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .then(AnimationUtils.cardSelectionModifier(isSelected)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) 
                MaterialTheme.colorScheme.primaryContainer 
            else 
                MaterialTheme.colorScheme.surface
        ),
        onClick = {
            onSelect()
            showDetails = !showDetails
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = carType,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    AnimatedVisibility(
                        visible = !showDetails,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        Text(
                            text = description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                // Price Section with Animation
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "₹${originalPrice.toInt()}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textDecoration = TextDecoration.LineThrough
                    )
                    Text(
                        text = "₹${discountedPrice.toInt()}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Save ₹${(originalPrice - discountedPrice).toInt()}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Green
                    )
                }
            }

            // Expandable Details
            AnimatedVisibility(
                visible = showDetails,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    // Capacity Info
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        CapacityInfo(
                            icon = Icons.Default.Person,
                            value = seats,
                            label = "Seats"
                        )
                        CapacityInfo(
                            icon = Icons.Default.Luggage,
                            value = bags,
                            label = "Bags"
                        )
                    }

                    // Features
                    Column(
                        modifier = Modifier.padding(top = 16.dp)
                    ) {
                        Text(
                            text = "Features:",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text("• Air Conditioning")
                        Text("• GPS Navigation")
                        Text("• 24/7 Support")
                    }
                }
            }
        }
    }
}

@Composable
private fun CapacityInfo(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: Int,
    label: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
        Column {
            Text(
                text = value.toString(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
