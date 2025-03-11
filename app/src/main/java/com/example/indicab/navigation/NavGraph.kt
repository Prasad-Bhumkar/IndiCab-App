package com.example.indicab.navigation

import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.indicab.ui.screens.*

@Composable
fun NavGraph(
    navController: NavHostController,
    onMenuClick: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = NavDestination.Home.route
    ) {
        composable(NavDestination.Home.route) {
            HomeScreen(
                navController = navController,
                onMenuClick = onMenuClick
            )
        }

        composable(NavDestination.BookRide.route) {
            EnhancedBookRideScreen(
                navController = navController,
                onMenuClick = onMenuClick
            )
        }

        composable(NavDestination.Profile.route) {
            ProfileScreen(
                navController = navController,
                onMenuClick = onMenuClick
            )
        }

        composable(
            route = NavDestinationWithArguments.Payment.route + "/{fare}",
            arguments = NavDestinationWithArguments.Payment.arguments
        ) { backStackEntry ->
            val fare = backStackEntry.arguments?.getFloat("fare") ?: 0f
            BookingConfirmationScreen(
                navController = navController,
                estimatedFare = fare.toDouble()
            )
        }

        composable(NavDestinationWithArguments.RideTracking.route) {
            BookingDetailsScreen(
                navController = navController,
                bookingId = "SCOWBI0001124" // This would typically come from state management
            )
        }

        composable(NavDestinationWithArguments.Booking.route) {
            BookingNotificationsScreen(
                navController = navController,
                onMenuClick = onMenuClick
            )
        }

        // Add other routes as needed
        composable(NavDestination.Settings.route) {
            // Settings screen implementation
        }
    }
}

// Update NavDestination to include all our routes
sealed class NavDestination(val route: String) {
    object Home : NavDestination("home")
    object BookRide : NavDestination("book_ride")
    object Profile : NavDestination("profile")
    object Settings : NavDestination("settings")
    object Notifications : NavDestination("notifications")
    object Wallet : NavDestination("wallet")
    object Support : NavDestination("support")
    object InviteFriend : NavDestination("invite_friend")
}

sealed class NavDestinationWithArguments(
    val route: String,
    val arguments: List<androidx.navigation.NamedNavArgument> = emptyList()
) {
    object Payment : NavDestinationWithArguments(
        route = "payment",
        arguments = listOf(
            androidx.navigation.NavArgument.Builder()
                .setName("fare")
                .setType(androidx.navigation.NavType.FloatType)
                .build()
        )
    ) {
        fun createRoute(fare: Double) = "$route/${fare.toFloat()}"
    }
    
    object RideTracking : NavDestinationWithArguments("ride_tracking")
    object Booking : NavDestinationWithArguments("booking")
    object DriverProfile : NavDestinationWithArguments("driver_profile")
}
