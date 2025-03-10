package com.example.indicab.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Booking : Screen("booking")
    object Payment : Screen("payment/{amount}") {
        fun createRoute(amount: Double): String = "payment/$amount"
    }
    object Profile : Screen("profile")
    object History : Screen("history")
    object DriverProfile : Screen("driver_profile")
} 