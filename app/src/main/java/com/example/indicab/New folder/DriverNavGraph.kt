package com.example.indicab.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.indicab.screens.*

fun NavGraphBuilder.driverNavGraph(
    navController: NavHostController,
    onMenuClick: () -> Unit
) {
    navigation(
        startDestination = DriverNavDestinations.Dashboard.route,
        route = "driver"
    ) {
        composable(DriverNavDestinations.Dashboard.route) {
            DriverDashboardScreen(
                navController = navController
            )
        }

        composable(DriverNavDestinations.Earnings.route) {
            DriverEarningsScreen(
                navController = navController
            )
        }

        composable(DriverNavDestinations.Documents.route) {
            DriverDocumentsScreen(
                navController = navController
            )
        }

        composable(DriverNavDestinations.Performance.route) {
            DriverPerformanceScreen(
                navController = navController
            )
        }

        composable(DriverNavDestinations.Preferences.route) {
            DriverPreferencesScreen(
                navController = navController
            )
        }

        composable(DriverNavDestinations.VehicleDetails.route) {
            VehicleDetailsScreen(
                navController = navController
            )
        }

        // History screens
        composable(DriverNavDestinations.EarningsHistory.route) {
            DriverEarningsHistoryScreen(
                navController = navController
            )
        }

        composable(DriverNavDestinations.DocumentsHistory.route) {
            DriverDocumentsHistoryScreen(
                navController = navController
            )
        }

        composable(DriverNavDestinations.PerformanceHistory.route) {
            DriverPerformanceHistoryScreen(
                navController = navController
            )
        }
    }
}
