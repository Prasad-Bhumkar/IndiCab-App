package com.example.indicab.navigation

import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.indicab.models.*
import com.example.indicab.ui.screens.*
import com.google.gson.Gson
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

@Composable
fun NavGraph(
    navController: NavHostController,
    onMenuClick: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = NavDestinations.Home.route
    ) {
        composable(NavDestinations.Home.route) {
            HomeScreen(
                navController = navController,
                onMenuClick = onMenuClick
            )
        }

        composable(NavDestinations.BookRide.route) {
            EnhancedBookRideScreen(
                navController = navController,
                onMenuClick = onMenuClick
            )
        }

        composable(NavDestinations.Profile.route) {
            ProfileScreen(
                navController = navController,
                onMenuClick = onMenuClick
            )
        }

        composable(NavDestinations.TrackRide.route) {
            TrackRideScreen(
                navController = navController
            )
        }

        composable(NavDestinations.MapView.route) {
            MapViewScreen(
                navController = navController
            )
        }

        composable(
            route = NavDestinations.ScheduleRide.route,
            arguments = listOf(
                navArgument("bookingRequest") { type = NavType.String }
            )
        ) { backStackEntry ->
            val bookingRequestJson = backStackEntry.arguments?.getString("bookingRequest")
            val bookingRequest = bookingRequestJson?.let {
                val decoded = URLDecoder.decode(it, StandardCharsets.UTF_8.toString())
                Gson().fromJson(decoded, BookingRequest::class.java)
            }
            
            bookingRequest?.let {
                ScheduleRideScreen(
                    bookingRequest = it,
                    onScheduleComplete = {
                        navController.navigate(NavDestinations.Home.route) {
                            popUpTo(NavDestinations.Home.route) { inclusive = true }
                        }
                    },
                    onBackPressed = { navController.popBackStack() }
                )
            }
        }

        composable(
            route = NavDestinations.Payment.route,
            arguments = listOf(
                navArgument("bookingId") { 
                    type = NavType.String
                    nullable = true
                    defaultValue = null
                },
                navArgument("amount") { type = NavType.FloatType }
            )
        ) { backStackEntry ->
            val bookingId = backStackEntry.arguments?.getString("bookingId")?.takeIf { it != "null" }
            val amount = backStackEntry.arguments?.getFloat("amount") ?: 0f
            
            PaymentScreen(
                navController = navController,
                bookingId = bookingId,
                amount = amount.toDouble()
            )
        }

        composable(
            route = NavDestinations.Chat.route,
            arguments = listOf(
                navArgument("bookingId") { type = NavType.String }
            )
        ) { backStackEntry ->
            val bookingId = backStackEntry.arguments?.getString("bookingId") ?: return@composable
            
            ChatScreen(
                navController = navController,
                bookingId = bookingId
            )
        }

        composable(
            route = NavDestinations.Rating.route,
            arguments = listOf(
                navArgument("bookingId") { type = NavType.String },
                navArgument("toUserId") { type = NavType.String },
                navArgument("ratingType") { type = NavType.String }
            )
        ) { backStackEntry ->
            val bookingId = backStackEntry.arguments?.getString("bookingId") ?: return@composable
            val toUserId = backStackEntry.arguments?.getString("toUserId") ?: return@composable
            val ratingType = backStackEntry.arguments?.getString("ratingType")?.let {
                try {
                    RatingType.valueOf(it)
                } catch (e: IllegalArgumentException) {
                    null
                }
            } ?: return@composable

            RatingScreen(
                navController = navController,
                bookingId = bookingId,
                toUserId = toUserId,
                ratingType = ratingType
            )
        }

        composable(
            route = NavDestinations.Emergency.route,
            arguments = listOf(
                navArgument("bookingId") {
                    type = NavType.String
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val bookingId = backStackEntry.arguments?.getString("bookingId")?.takeIf { it != "null" }
            
            EmergencyScreen(
                navController = navController,
                bookingId = bookingId
            )
        }

        composable(NavDestinations.ThemeSettings.route) {
            ThemeSettingsScreen(
                navController = navController
            )
        }
    }
}
