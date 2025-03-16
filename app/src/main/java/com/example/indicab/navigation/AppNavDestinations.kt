package com.example.indicab.navigation

sealed class NavDestinations(val route: String) {
    object Home : NavDestinations("home")
    object BookRide : NavDestinations("book_ride")
    object TrackRide : NavDestinations("track_ride")
    object Profile : NavDestinations("profile")
    object MapView : NavDestinations("map_view")
    object ScheduleRide : NavDestinations("schedule_ride/{bookingRequest}") {
        fun createRoute(bookingRequestJson: String): String =
            route.replace("{bookingRequest}", bookingRequestJson)
    }
    object Payment : NavDestinations("payment/{bookingId}/{amount}") {
        fun createRoute(bookingId: String?, amount: Double): String =
            "payment/${bookingId ?: "null"}/$amount"
    }
}
