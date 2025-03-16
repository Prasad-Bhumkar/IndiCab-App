<<<<<<< HEAD
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
=======
package com.example.indicab.navigation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.indicab.screens.MonitoringDashboardScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = NavDestinations.Home.route) {
        composable(NavDestinations.Home.route) {
            // HomeScreen() // Replace with actual HomeScreen composable
        }
        composable(NavDestinations.BookRide.route) {
            // BookRideScreen() // Replace with actual BookRideScreen composable
        }
        composable(NavDestinations.MonitoringDashboard.route) {
            MonitoringDashboardScreen()
        }
        // Add other destinations here
    }
}
>>>>>>> 81ec31f166cdb0573d5c5135fcdecb0f6ba49d83
