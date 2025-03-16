package com.example.indicab

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.navigation.compose.rememberNavController
<<<<<<< HEAD
import com.example.indicab.navigation.NavDestination
import com.example.indicab.ui.screens.*
=======
import com.example.indicab.navigation.NavigationGraph
import dagger.hilt.android.AndroidEntryPoint
>>>>>>> 81ec31f166cdb0573d5c5135fcdecb0f6ba49d83

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            NavigationGraph(navController = navController)
        }
    }
}
