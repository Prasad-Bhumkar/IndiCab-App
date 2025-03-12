package com.example.indicab.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.indicab.screens.*

@Composable
fun SimpleNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = NavDestinations.Home.route
    ) {
        // User Flow
        composable(NavDestinations.Home.route) {
            SimpleHomeScreen(
                onBookRide = {
                    navController.navigate(NavDestinations.BookRide.route)
                }
            )
        }

        composable(NavDestinations.BookRide.route) {
            SimpleBookingScreen(
                onConfirmBooking = {
                    navController.navigate(NavDestinations.Payment.createRoute(null, 299.0)) {
                        popUpTo(NavDestinations.Home.route) {
                            inclusive = false
                        }
                    }
                }
            )
        }

        composable(
            route = NavDestinations.Payment.route,
            arguments = listOf(
                navArgument("bookingId") { nullable = true },
                navArgument("amount") { type = NavType.FloatType }
            )
        ) {
            BookingConfirmationScreen(
                onBackToHome = {
                    navController.navigate(NavDestinations.Home.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // Driver Flow
        composable(NavDestinations.DriverDashboard.route) {
            SimpleDriverDashboardScreen(
                onNavigateToRide = {
                    navController.navigate(NavDestinations.DriverRide.route)
                },
                onNavigateToEarnings = {
                    navController.navigate(NavDestinations.DriverEarnings.route)
                },
                onNavigateToDocuments = {
                    navController.navigate(NavDestinations.DriverDocuments.route)
                }
            )
        }

        // Tracking Flow
        composable(NavDestinations.TrackRide.route) {
            TrackRideScreen(
                onBackPressed = {
                    navController.navigateUp()
                }
            )
        }

        // Chat Flow
        composable(
            route = NavDestinations.Chat.route,
            arguments = listOf(
                navArgument("bookingId") { type = NavType.StringType }
            )
        ) {
            ChatScreen(
                onBackPressed = {
                    navController.navigateUp()
                }
            )
        }

        // Theme Settings
        composable(NavDestinations.ThemeSettings.route) {
            ThemeSettingsScreen(
                onBackPressed = {
                    navController.navigateUp()
                }
            )
        }
    }
}
