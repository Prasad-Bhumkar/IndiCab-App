package com.example.indicab.navigation

sealed class NavDestinations(val route: String) {
    object Home : NavDestinations("home")
    object BookRide : NavDestinations("book_ride")
    object TrackRide : NavDestinations("track_ride")
    object Payment : NavDestinations("payment")
    object Profile : NavDestinations("profile")
    object MapView : NavDestinations("map_view")
    object ScheduleRide : NavDestinations("schedule_ride/{bookingRequest}") {
        fun createRoute(bookingRequestJson: String): String =
            route.replace("{bookingRequest}", bookingRequestJson)
    }
}
