package com.example.indicab.navigation

import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.indicab.ui.screens.*

@Composable
fun MainNavGraph(
    navController: NavHostController,
    onMenuClick: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = NavDestinations.Home.route
    ) {
        composable(NavDestinations.Home.route) {
            HomeScreen(
                navController = navController,
                onMenuClick = onMenuClick
            )
        }

        // Other existing routes...

        // Driver-related navigation
        DriverNavGraph(navController)
    }
}
