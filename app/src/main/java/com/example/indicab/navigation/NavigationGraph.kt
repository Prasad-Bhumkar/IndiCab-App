 package com.example.indicab.navigation
 
 import androidx.compose.runtime.Composable
 import androidx.navigation.NavHostController
 import androidx.navigation.compose.NavHost
 import androidx.navigation.compose.composable
 import com.example.indicab.screens.ChatScreen
 import com.example.indicab.ui.screens.BookRideScreen
 import com.example.indicab.ui.screens.HomeScreen
 import com.example.indicab.ui.screens.ProfileScreen
 import com.example.indicab.ui.screens.TrackRideScreen
 import com.google.firebase.auth.FirebaseAuth
 
 @Composable
 fun NavigationGraph(
     navController: NavHostController,
     startDestination: String = NavDestination.Home.route
 ) {
     NavHost(
         navController = navController,
         startDestination = startDestination
     ) {
         composable(NavDestination.Home.route) {
             HomeScreen(navController)
         }
         
         composable(NavDestination.BookRide.route) {
             BookRideScreen(navController)
         }
         
         composable(NavDestination.TrackRide.route) {
             TrackRideScreen(navController)
         }
         
         composable(NavDestination.Profile.route) {
             ProfileScreen(navController)
         }
 
         composable(
             route = NavDestinationWithArguments.Chat.route + "/{otherUserId}",
             arguments = NavDestinationWithArguments.Chat.arguments
         ) { backStackEntry ->
             val otherUserId = backStackEntry.arguments?.getString("otherUserId") ?: return@composable
             val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return@composable
             
             ChatScreen(
                 currentUserId = currentUserId,
                 otherUserId = otherUserId,
                 onBackClick = { navController.popBackStack() }
             )
         }
     }
 } 