package com.example.indicab.navigation

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument

sealed class NavDestination(val route: String) {
    object Home : NavDestination("home")
    object BookRide : NavDestination("book_ride")
    object TrackRide : NavDestination("track_ride")
    object Profile : NavDestination("profile")
    object Settings : NavDestination("settings")
}

sealed class NavDestinationWithArguments(
    val route: String,
    val arguments: List<NamedNavArgument> = emptyList()
) {
    object Home : NavDestinationWithArguments("home")
    object Booking : NavDestinationWithArguments("booking")
    object Payment : NavDestinationWithArguments(
        route = "payment",
        arguments = listOf(
            navArgument("fare") {
                type = NavType.FloatType
            }
        )
    ) {
        fun createRoute(fare: Double) = "$route/${fare.toFloat()}"
    }
    object RideTracking : NavDestinationWithArguments("ride_tracking")
    object DriverProfile : NavDestinationWithArguments("driver_profile")
    object Chat : NavDestinationWithArguments(
        route = "chat",
        arguments = listOf(
            navArgument("otherUserId") {
                type = NavType.StringType
            }
        )
    ) {
        fun createRoute(otherUserId: String) = "$route/$otherUserId"
    }
    
    companion object {
        fun fromRoute(route: String): NavDestinationWithArguments {
            return when (route) {
                "home" -> Home
                "booking" -> Booking
                "ride_tracking" -> RideTracking
                "driver_profile" -> DriverProfile
                else -> {
                    when {
                        route.startsWith("payment/") -> Payment
                        route.startsWith("chat/") -> Chat
                        else -> Home
                    }
                }
            }
        }
    }
} 