package com.example.indicab.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.indicab.ui.screens.*

@Composable
fun NavigationSetup(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = NavDestination.HomeScreen.route
    ) {
        composable(NavDestination.HomeScreen.route) {
            HomeScreen()
        }
        composable(NavDestination.BookingScreen.route) {
            BookingScreen()
        }
        composable(NavDestination.PaymentScreen.route) {
            PaymentScreen()
        }
        composable(NavDestination.ProfileScreen.route) {
            ProfileScreen()
        }
    }
}
