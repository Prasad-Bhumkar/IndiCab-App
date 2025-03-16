 package com.example.indicab.ui.screens
 
 import androidx.compose.animation.*
 import androidx.compose.foundation.layout.*
 import androidx.compose.foundation.lazy.LazyRow
 import androidx.compose.foundation.lazy.items
 import androidx.compose.foundation.rememberScrollState
 import androidx.compose.foundation.verticalScroll
 import androidx.compose.material.icons.Icons
 import androidx.compose.material.icons.filled.*
 import androidx.compose.material3.*
 import androidx.compose.runtime.*
 import androidx.compose.ui.Alignment
 import androidx.compose.ui.Modifier
 import androidx.compose.ui.text.style.TextAlign
 import androidx.compose.ui.unit.dp
 import androidx.lifecycle.viewmodel.compose.viewModel
 import androidx.navigation.NavController
 import com.example.indicab.models.*
 import com.example.indicab.viewmodels.RatingState
 import com.example.indicab.viewmodels.RatingViewModel
 
 @OptIn(ExperimentalMaterial3Api::class)
 @Composable
 fun RatingScreen(
     navController: NavController,
     bookingId: String,
     toUserId: String,
     ratingType: RatingType,
     ratingViewModel: RatingViewModel = viewModel()
 ) {
     val ratingState by ratingViewModel.ratingState.collectAsState()
     val selectedTags by ratingViewModel.selectedTags.collectAsState()
     val ratingScore by ratingViewModel.ratingScore.collectAsState()
     val review by ratingViewModel.review.collectAsState()
     val positiveTags by ratingViewModel.positiveTags.collectAsState()
     val negativeTags by ratingViewModel.negativeTags.collectAsState()
 
     LaunchedEffect(ratingState) {
         when (ratingState) {
             is RatingState.Success -> {
                 // Navigate back or to confirmation screen
                 navController.popBackStack()
             }
             is RatingState.Skipped -> {
                 navController.popBackStack()
             }
             else -> {}
         }
     }
 
     Scaffold(
         topBar = {
             TopAppBar(
                 title = { Text("Rate Your Ride") },
                 navigationIcon = {
                     IconButton(onClick = { navController.popBackStack() }) {
                         Icon(Icons.Default.ArrowBack, "Back")
                     }
                 }
             )
         }
     ) { padding ->
         Box(
             modifier = Modifier
                 .fillMaxSize()
                 .padding(padding)
         ) {
             when (ratingState) {
                 is RatingState.Submitting -> {
                     CircularProgressIndicator(
                         modifier = Modifier.align(Alignment.Center)
                     )
                 }
                 is RatingState.Error -> {
                     ErrorView(
                         error = (ratingState as RatingState.Error).message,
                         onRetry = { ratingViewModel.resetState() }
                     )
                 }
                 else -> {
                     Column(
                         modifier = Modifier
                             .fillMaxSize()
                             .verticalScroll(rememberScrollState())
                             .padding(16.dp),
                         horizontalAlignment = Alignment.CenterHorizontally,
                         verticalArrangement = Arrangement.spacedBy(24.dp)
                     ) {
                         // Rating Stars
                         RatingStars(
                             score = ratingScore ?: 0f,
                             onRatingChanged = { ratingViewModel.setRatingScore(it) }
                         )
 
                         // Tags Section
                         if (ratingScore != null) {
                             TagsSection(
                                 positiveTags = positiveTags,
                                 negativeTags = negativeTags,
                                 selectedTags = selectedTags,
                                 onTagSelected = { ratingViewModel.toggleTag(it) },
                                 showPositive = ratingScore!! >= 3.0f
                             )
                         }
 
                         // Review Input
                         OutlinedTextField(
                             value = review,
                             onValueChange = { ratingViewModel.setReview(it) },
                             modifier = Modifier.fillMaxWidth(),
                             label = { Text("Additional Comments") },
                             minLines = 3,
                             maxLines = 5
                         )
 
                         // Submit Button
                         Button(
                             onClick = {
                                 ratingViewModel.submitRating(toUserId, ratingType)
                             },
                             modifier = Modifier.fillMaxWidth(),
                             enabled = ratingScore != null
                         ) {
                             Text("Submit Rating")
                         }
 
                         // Skip Button
                         TextButton(
                             onClick = { ratingViewModel.skipRating(bookingId) },
                             modifier = Modifier.fillMaxWidth()
                         ) {
                             Text("Skip")
                         }
                     }
                 }
             }
         }
     }
 }
 
 @Composable
 private fun RatingStars(
     score: Float,
     onRatingChanged: (Float) -> Unit,
     modifier: Modifier = Modifier
 ) {
     Column(
         modifier = modifier,
         horizontalAlignment = Alignment.CenterHorizontally
     ) {
         Text(
             text = "How was your ride?",
             style = MaterialTheme.typography.titleLarge,
             modifier = Modifier.padding(bottom = 16.dp)
         )
 
         Row(
             horizontalArrangement = Arrangement.spacedBy(8.dp)
         ) {
             repeat(5) { index ->
                 val starFilled = index < score
                 IconButton(
                     onClick = { onRatingChanged(index + 1f) }
                 ) {
                     Icon(
                         imageVector = if (starFilled) {
                             Icons.Default.Star
                         } else {
                             Icons.Default.StarBorder
                         },
                         contentDescription = "Star ${index + 1}",
                         tint = if (starFilled) {
                             MaterialTheme.colorScheme.primary
                         } else {
                             MaterialTheme.colorScheme.onSurfaceVariant
                         }
                     )
                 }
             }
         }
 
         if (score > 0) {
             Text(
                 text = when {
                     score >= 4.5f -> "Excellent!"
                     score >= 3.5f -> "Good"
                     score >= 2.5f -> "Okay"
                     else -> "Poor"
                 },
                 style = MaterialTheme.typography.titleMedium,
                 modifier = Modifier.padding(top = 8.dp)
             )
         }
     }
 }
 
 @OptIn(ExperimentalMaterial3Api::class)
 @Composable
 private fun TagsSection(
     positiveTags: List<RatingTag>,
     negativeTags: List<RatingTag>,
     selectedTags: Set<String>,
     onTagSelected: (String) -> Unit,
     showPositive: Boolean
 ) {
     Column(
         modifier = Modifier.fillMaxWidth(),
         verticalArrangement = Arrangement.spacedBy(16.dp)
     ) {
         Text(
             text = if (showPositive) {
                 "What did you like?"
             } else {
                 "What went wrong?"
             },
             style = MaterialTheme.typography.titleMedium
         )
 
         LazyRow(
             horizontalArrangement = Arrangement.spacedBy(8.dp)
         ) {
             items(if (showPositive) positiveTags else negativeTags) { tag ->
                 val isSelected = selectedTags.contains(tag.text)
                 FilterChip(
                     selected = isSelected,
                     onClick = { onTagSelected(tag.text) },
                     label = { Text(tag.text) },
                     leadingIcon = if (isSelected) {
                         {
                             Icon(
                                 Icons.Default.Check,
                                 contentDescription = null,
                                 modifier = Modifier.size(16.dp)
                             )
                         }
                     } else null
                 )
             }
         }
     }
 }
 
 @Composable
 private fun ErrorView(
     error: String,
     onRetry: () -> Unit
 ) {
     Column(
         modifier = Modifier
             .fillMaxSize()
             .padding(16.dp),
         horizontalAlignment = Alignment.CenterHorizontally,
         verticalArrangement = Arrangement.Center
     ) {
         Text(
             text = error,
             style = MaterialTheme.typography.bodyLarge,
             textAlign = TextAlign.Center
         )
         Spacer(modifier = Modifier.height(16.dp))
         Button(onClick = onRetry) {
             Text("Try Again")
         }
     }
 }
