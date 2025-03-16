package com.example.indicab.navigation

import androidx.navigation.NavGraph
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

fun NavGraphBuilder.appNavigationGraph(navController: NavHostController) {
    composable(route = "home") { /* Home Screen */ }
    composable(route = "book_ride") { /* Book Ride Screen */ }
    composable(route = "track_ride") { /* Track Ride Screen */ }
    composable(route = "profile") { /* Profile Screen */ }
    composable(route = "map_view") { /* Map View Screen */ }
    composable(route = "schedule_ride/{bookingRequest}") {
        // Extract bookingRequest from argument
        val bookingRequest = it.arguments?.getString("bookingRequest")
        /* Schedule Ride Screen */
    }
    composable(route = "chat/{bookingId}") {
        val bookingId = it.arguments?.getString("bookingId")
        /* Chat Screen */
    }
    composable(route = "payment/{bookingId}/{amount}") {
        val bookingId = it.arguments?.getString("bookingId")
        val amount = it.arguments?.getString("amount")
        /* Payment Screen */
    }
    composable(route = "rating/{bookingId}/{toUserId}/{ratingType}") {
        val bookingId = it.arguments?.getString("bookingId")
        val toUserId = it.arguments?.getString("toUserId")
        val ratingType = it.arguments?.getString("ratingType")
        /* Rating Screen */
    }
}
