package com.example.indicab.navigation

sealed class NavDestinations(val route: String) {
    // User Flow
    object Home : NavDestinations("home")
    object BookRide : NavDestinations("book_ride")
    object Payment : NavDestinations("payment/{bookingId}/{amount}") {
        fun createRoute(bookingId: String?, amount: Double): String =
            "payment/${bookingId ?: "null"}/$amount"
    }
    object TrackRide : NavDestinations("track_ride")

    // Driver Flow
    object DriverDashboard : NavDestinations("driver_dashboard")
    object DriverRide : NavDestinations("driver_ride")
    object DriverEarnings : NavDestinations("driver_earnings")
    object DriverDocuments : NavDestinations("driver_documents")

    // Common Features
    object Chat : NavDestinations("chat/{bookingId}") {
        fun createRoute(bookingId: String): String =
            route.replace("{bookingId}", bookingId)
    }
    object ThemeSettings : NavDestinations("theme_settings")
    object Emergency : NavDestinations("emergency/{bookingId}") {
        fun createRoute(bookingId: String? = null): String =
            "emergency/${bookingId ?: "null"}"
    }

    // Monitoring
    object MonitoringDashboard : NavDestinations("monitoring_dashboard")
}
