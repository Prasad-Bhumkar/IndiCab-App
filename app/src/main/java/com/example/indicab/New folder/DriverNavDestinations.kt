package com.example.indicab.navigation

sealed class DriverNavDestinations(val route: String) {
    object Dashboard : DriverNavDestinations("driver/dashboard")
    object Earnings : DriverNavDestinations("driver/earnings")
    object Documents : DriverNavDestinations("driver/documents")
    object Performance : DriverNavDestinations("driver/performance")
    object Preferences : DriverNavDestinations("driver/preferences")
    object VehicleDetails : DriverNavDestinations("driver/vehicle")
    
    // History screens
    object EarningsHistory : DriverNavDestinations("driver/earnings/history")
    object DocumentsHistory : DriverNavDestinations("driver/documents/history")
    object PerformanceHistory : DriverNavDestinations("driver/performance/history")
}
