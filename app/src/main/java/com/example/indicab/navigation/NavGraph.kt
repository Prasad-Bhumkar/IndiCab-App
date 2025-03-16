package com.example.indicab.navigation

import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.indicab.ui.screens.*
import com.example.indicab.models.BookingRequest
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
            BookRideScreen(
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

        composable(NavDestinations.Payment.route) {
            PaymentScreen(
                navController = navController
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
    }
}
