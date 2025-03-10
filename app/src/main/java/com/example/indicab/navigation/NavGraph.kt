package com.example.indicab.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.indicab.ui.BookingScreen
import com.example.indicab.ui.PaymentScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Booking.route
    ) {
        composable(Screen.Booking.route) {
            BookingScreen(navController = navController)
        }
        
        composable(
            route = Screen.Payment.route,
            arguments = listOf(
                navArgument("amount") {
                    type = NavType.FloatType
                }
            )
        ) { backStackEntry ->
            val amount = backStackEntry.arguments?.getFloat("amount")?.toDouble() ?: 0.0
            PaymentScreen(navController = navController, amount = amount)
        }
    }
} 