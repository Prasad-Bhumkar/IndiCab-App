package com.example.indicab.navigation

sealed class NavDestinations(val route: String) {
    object Home : NavDestinations("home")
    object BookRide : NavDestinations("book_ride")
    object TrackRide : NavDestinations("track_ride")
    object Payment : NavDestinations("payment")
    object Profile : NavDestinations("profile")
    object MapView : NavDestinations("map_view")
}
