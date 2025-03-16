 package com.example.indicab.utils
 
 import androidx.compose.animation.*
 import androidx.compose.animation.core.*
 import androidx.compose.runtime.Composable
 import androidx.compose.ui.Modifier
 import androidx.compose.ui.graphics.TransformOrigin
 import androidx.compose.ui.unit.dp
 
 object AnimationUtils {
     // Slide in/out animations for screen transitions
     fun enterTransition() = slideInHorizontally(
         initialOffsetX = { fullWidth -> fullWidth },
         animationSpec = tween(
             durationMillis = 300,
             easing = FastOutSlowInEasing
         )
     ) + fadeIn(
         animationSpec = tween(300)
     )
 
     fun exitTransition() = slideOutHorizontally(
         targetOffsetX = { fullWidth -> -fullWidth },
         animationSpec = tween(
             durationMillis = 300,
             easing = FastOutSlowInEasing
         )
     ) + fadeOut(
         animationSpec = tween(300)
     )
 
     fun popEnterTransition() = slideInHorizontally(
         initialOffsetX = { fullWidth -> -fullWidth },
         animationSpec = tween(
             durationMillis = 300,
             easing = FastOutSlowInEasing
         )
     ) + fadeIn(
         animationSpec = tween(300)
     )
 
     fun popExitTransition() = slideOutHorizontally(
         targetOffsetX = { fullWidth -> fullWidth },
         animationSpec = tween(
             durationMillis = 300,
             easing = FastOutSlowInEasing
         )
     ) + fadeOut(
         animationSpec = tween(300)
     )
 
     // Card selection animation
     @Composable
     fun cardSelectionModifier(selected: Boolean): Modifier {
         val scale = animateFloatAsState(
             targetValue = if (selected) 1.02f else 1f,
             animationSpec = spring(
                 dampingRatio = Spring.DampingRatioMediumBouncy,
                 stiffness = Spring.StiffnessLow
             )
         )
         
         val elevation = animateDpAsState(
             targetValue = if (selected) 8.dp else 2.dp,
             animationSpec = tween(
                 durationMillis = 300,
                 easing = FastOutSlowInEasing
             )
         )
 
         return Modifier
             .graphicsLayer {
                 scaleX = scale.value
                 scaleY = scale.value
                 this.shadowElevation = elevation.value.toPx()
             }
     }
 
     // Drawer animation specs
     val drawerTransitionSpec = tween<Float>(
         durationMillis = 400,
         easing = FastOutSlowInEasing
     )
 
     // Loading animation
     @Composable
     fun pulseAnimation(
         pulseFraction: Float = 1.2f
     ): Modifier {
         val infiniteTransition = rememberInfiniteTransition()
         val scale by infiniteTransition.animateFloat(
             initialValue = 1f,
             targetValue = pulseFraction,
             animationSpec = infiniteRepeatable(
                 animation = tween(1000),
                 repeatMode = RepeatMode.Reverse
             )
         )
 
         return Modifier.graphicsLayer {
             scaleX = scale
             scaleY = scale
         }
     }
 
     // Content fade through transition
     @Composable
     fun fadeThrough(
         visible: Boolean,
         content: @Composable AnimatedVisibilityScope.() -> Unit
     ) {
         AnimatedVisibility(
             visible = visible,
             enter = fadeIn(
                 animationSpec = tween(
                     durationMillis = 300,
                     delayMillis = 100,
                     easing = LinearOutSlowInEasing
                 )
             ),
             exit = fadeOut(
                 animationSpec = tween(
                     durationMillis = 300,
                     easing = FastOutLinearInEasing
                 )
             ),
             content = content
         )
     }
 
     // Button press animation
     @Composable
     fun buttonPressAnimation(pressed: Boolean): Modifier {
         val scale = animateFloatAsState(
             targetValue = if (pressed) 0.95f else 1f,
             animationSpec = spring(
                 dampingRatio = Spring.DampingRatioMediumBouncy,
                 stiffness = Spring.StiffnessLow
             )
         )
 
         return Modifier.graphicsLayer {
             scaleX = scale.value
             scaleY = scale.value
             transformOrigin = TransformOrigin.Center
         }
     }
 }
