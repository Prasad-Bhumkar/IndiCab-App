 package com.example.indicab.components
 
 import androidx.compose.animation.*
 import androidx.compose.animation.core.*
 import androidx.compose.foundation.background
 import androidx.compose.foundation.layout.*
 import androidx.compose.foundation.shape.RoundedCornerShape
 import androidx.compose.material.icons.Icons
 import androidx.compose.material.icons.filled.ArrowForward
 import androidx.compose.material.icons.filled.CompareArrows
 import androidx.compose.material.icons.filled.TimeToLeave
 import androidx.compose.material3.*
 import androidx.compose.runtime.*
 import androidx.compose.ui.Alignment
 import androidx.compose.ui.Modifier
 import androidx.compose.ui.draw.clip
 import androidx.compose.ui.graphics.Color
 import androidx.compose.ui.graphics.graphicsLayer
 import androidx.compose.ui.graphics.vector.ImageVector
 import androidx.compose.ui.text.font.FontWeight
 import androidx.compose.ui.unit.dp
 import com.example.indicab.utils.AnimationUtils
 
 enum class TripType {
     ONEWAY, ROUND_TRIP, RENTAL
 }
 
 @Composable
 fun EnhancedTripTypeSelector(
     selectedType: TripType,
     onTypeSelected: (TripType) -> Unit,
     modifier: Modifier = Modifier
 ) {
     val types = listOf(
         Triple(TripType.ONEWAY, "ONEWAY", Icons.Default.ArrowForward),
         Triple(TripType.ROUND_TRIP, "ROUND TRIP", Icons.Default.CompareArrows),
         Triple(TripType.RENTAL, "RENTAL", Icons.Default.TimeToLeave)
     )
 
     Surface(
         modifier = modifier
             .fillMaxWidth()
             .clip(RoundedCornerShape(16.dp)),
         color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
         tonalElevation = 2.dp
     ) {
         Row(
             modifier = Modifier
                 .fillMaxWidth()
                 .padding(4.dp),
             horizontalArrangement = Arrangement.SpaceEvenly
         ) {
             types.forEach { (type, label, icon) ->
                 TripTypeButton(
                     type = type,
                     label = label,
                     icon = icon,
                     isSelected = type == selectedType,
                     onSelect = { onTypeSelected(type) },
                     modifier = Modifier.weight(1f)
                 )
             }
         }
     }
 }
 
 @Composable
 private fun TripTypeButton(
     type: TripType,
     label: String,
     icon: ImageVector,
     isSelected: Boolean,
     onSelect: () -> Unit,
     modifier: Modifier = Modifier
 ) {
     val backgroundColor = if (isSelected) {
         MaterialTheme.colorScheme.primary
     } else {
         Color.Transparent
     }
 
     val contentColor = if (isSelected) {
         MaterialTheme.colorScheme.onPrimary
     } else {
         MaterialTheme.colorScheme.onSurfaceVariant
     }
 
     val scale by animateFloatAsState(
         targetValue = if (isSelected) 1f else 0.9f,
         animationSpec = spring(
             dampingRatio = Spring.DampingRatioMediumBouncy,
             stiffness = Spring.StiffnessLow
         )
     )
 
     Surface(
         onClick = onSelect,
         modifier = modifier
             .padding(4.dp)
             .graphicsLayer {
                 scaleX = scale
                 scaleY = scale
             },
         shape = RoundedCornerShape(12.dp),
         color = backgroundColor
     ) {
         Column(
             modifier = Modifier
                 .padding(vertical = 12.dp, horizontal = 8.dp),
             horizontalAlignment = Alignment.CenterHorizontally
         ) {
             Icon(
                 imageVector = icon,
                 contentDescription = label,
                 tint = contentColor,
                 modifier = Modifier.size(24.dp)
             )
             
             Spacer(modifier = Modifier.height(4.dp))
             
             Text(
                 text = label,
                 style = MaterialTheme.typography.labelMedium,
                 fontWeight = FontWeight.Medium,
                 color = contentColor
             )
 
             // Animated indicator
             AnimatedVisibility(
                 visible = isSelected,
                 enter = fadeIn() + expandVertically(),
                 exit = fadeOut() + shrinkVertically()
             ) {
                 Box(
                     modifier = Modifier
                         .padding(top = 4.dp)
                         .size(4.dp)
                         .background(
                             color = contentColor,
                             shape = RoundedCornerShape(2.dp)
                         )
                 )
             }
         }
     }
 }
 
 @OptIn(ExperimentalAnimationApi::class)
 @Composable
 fun TripTypeDescription(type: TripType) {
     AnimatedContent(
         targetState = type,
         transitionSpec = {
             fadeIn(animationSpec = tween(300)) with
             fadeOut(animationSpec = tween(300))
         }
     ) { targetType ->
         Text(
             text = when (targetType) {
                 TripType.ONEWAY -> "One-way trip to your destination"
                 TripType.ROUND_TRIP -> "Return journey included"
                 TripType.RENTAL -> "Hourly rental with unlimited distance"
             },
             style = MaterialTheme.typography.bodyMedium,
             color = MaterialTheme.colorScheme.onSurfaceVariant,
             modifier = Modifier.padding(top = 8.dp)
         )
     }
 }
