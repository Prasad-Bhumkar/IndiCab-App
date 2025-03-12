package com.example.indicab.navigation

sealed class DriverDestinations(val route: String) {
    object DriverDashboard : DriverDestinations("driver_dashboard/{userId}") {
        fun createRoute(userId: String): String = "driver_dashboard/$userId"
    }
    object VehicleDetails : DriverDestinations("vehicle_details/{vehicleId}") {
        fun createRoute(vehicleId: String): String = "vehicle_details/$vehicleId"
    }
    object DriverPerformance : DriverDestinations("driver_performance/{driverId}") {
        fun createRoute(driverId: String): String = "driver_performance/$driverId"
    }
    object DriverEarnings : DriverDestinations("driver_earnings/{driverId}") {
        fun createRoute(driverId: String): String = "driver_earnings/$driverId"
    }
    object DriverDocuments : DriverDestinations("driver_documents/{driverId}") {
        fun createRoute(driverId: String): String = "driver_documents/$driverId"
    }
    object DriverPreferences : DriverDestinations("driver_preferences/{driverId}") {
        fun createRoute(driverId: String): String = "driver_preferences/$driverId"
    }
    object DriverEarningsHistory : DriverDestinations("driver_earnings_history/{driverId}") {
        fun createRoute(driverId: String): String = "driver_earnings_history/$driverId"
    }
    object DriverPerformanceHistory : DriverDestinations("driver_performance_history/{driverId}") {
        fun createRoute(driverId: String): String = "driver_performance_history/$driverId"
    }
    object DriverDocumentsHistory : DriverDestinations("driver_documents_history/{driverId}") {
        fun createRoute(driverId: String): String = "driver_documents_history/$driverId"
    }
}
