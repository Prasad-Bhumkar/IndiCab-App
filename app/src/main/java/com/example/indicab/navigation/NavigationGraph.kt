package com.example.indicab.navigation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.indicab.screens.MonitoringDashboardScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = NavDestinations.Home.route) {
        composable(NavDestinations.Home.route) {
            // HomeScreen() // Replace with actual HomeScreen composable
        }
        composable(NavDestinations.BookRide.route) {
            // BookRideScreen() // Replace with actual BookRideScreen composable
        }
        composable(NavDestinations.MonitoringDashboard.route) {
            MonitoringDashboardScreen()
        }
        // Add other destinations here
    }
}
