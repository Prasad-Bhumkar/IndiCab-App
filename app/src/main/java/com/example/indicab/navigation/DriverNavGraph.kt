package com.example.indicab.navigation

import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.indicab.models.*
import com.example.indicab.ui.screens.*

@Composable
fun DriverNavGraph(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = DriverDestinations.DriverDashboard.route
    ) {
        composable(
            route = DriverDestinations.DriverDashboard.route,
            arguments = listOf(
                navArgument("userId") { type = NavType.String }
            )
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: return@composable
            DriverDashboardScreen(
                navController = navController
            )
        }

        composable(
            route = DriverDestinations.VehicleDetails.route,
            arguments = listOf(
                navArgument("vehicleId") { type = NavType.String }
            )
        ) { backStackEntry ->
            val vehicleId = backStackEntry.arguments?.getString("vehicleId") ?: return@composable
            VehicleDetailsScreen(
                vehicleId = vehicleId,
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = DriverDestinations.DriverPerformance.route,
            arguments = listOf(
                navArgument("driverId") { type = NavType.String }
            )
        ) { backStackEntry ->
            val driverId = backStackEntry.arguments?.getString("driverId") ?: return@composable
            DriverPerformanceScreen(
                driverId = driverId,
                onBack = { navController.popBackStack() }
            )
        }
        
        composable(
            route = DriverDestinations.DriverEarnings.route,
            arguments = listOf(
                navArgument("driverId") { type = NavType.String }
            )
        ) { backStackEntry ->
            val driverId = backStackEntry.arguments?.getString("driverId") ?: return@composable
            DriverEarningsScreen(
                driverId = driverId,
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = DriverDestinations.DriverDocuments.route,
            arguments = listOf(
                navArgument("driverId") { type = NavType.String }
            )
        ) { backStackEntry ->
            val driverId = backStackEntry.arguments?.getString("driverId") ?: return@composable
            DriverDocumentsScreen(
                driverId = driverId,
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = DriverDestinations.DriverPreferences.route,
            arguments = listOf(
                navArgument("driverId") { type = NavType.String }
            )
        ) { backStackEntry ->
            val driverId = backStackEntry.arguments?.getString("driverId") ?: return@composable
            DriverPreferencesScreen(
                driverId = driverId,
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = DriverDestinations.DriverEarningsHistory.route,
            arguments = listOf(
                navArgument("driverId") { type = NavType.String }
            )
        ) { backStackEntry ->
            val driverId = backStackEntry.arguments?.getString("driverId") ?: return@composable
            DriverEarningsHistoryScreen(
                driverId = driverId,
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = DriverDestinations.DriverPerformanceHistory.route,
            arguments = listOf(
                navArgument("driverId") { type = NavType.String }
            )
        ) { backStackEntry ->
            val driverId = backStackEntry.arguments?.getString("driverId") ?: return@composable
            DriverPerformanceHistoryScreen(
                driverId = driverId,
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = DriverDestinations.DriverDocumentsHistory.route,
            arguments = listOf(
                navArgument("driverId") { type = NavType.String }
            )
        ) { backStackEntry ->
            val driverId = backStackEntry.arguments?.getString("driverId") ?: return@composable
            DriverDocumentsHistoryScreen(
                driverId = driverId,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
