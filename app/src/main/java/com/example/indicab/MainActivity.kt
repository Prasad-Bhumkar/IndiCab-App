package com.example.indicab

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.indicab.navigation.NavDestination
import com.example.indicab.ui.screens.*
import com.example.indicab.ui.theme.IndiCabTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            IndiCabTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = NavDestination.Home.route
                    ) {
                        composable(NavDestination.Home.route) {
                            HomeScreen(navController)
                        }
                        composable(NavDestination.BookRide.route) {
                            BookRideScreen(navController)
                        }
                        composable(NavDestination.TrackRide.route) {
                            TrackRideScreen(navController)
                        }
                        composable(NavDestination.Profile.route) {
                            DriverProfileScreen(navController)
                        }
                    }
                }
            }
        }
    }
}