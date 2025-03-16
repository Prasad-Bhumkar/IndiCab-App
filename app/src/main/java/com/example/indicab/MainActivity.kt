package com.example.indicab

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.navigation.compose.rememberNavController
import com.example.indicab.navigation.NavigationGraph
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            setContent {
                val navController = rememberNavController()
                NavigationGraph(navController = navController)
            }
            } catch (e: Exception) {
                // Log the exception with device information for better diagnostics
                val deviceInfo = "Device: ${Build.MANUFACTURER} ${Build.MODEL}, SDK: ${Build.VERSION.SDK_INT}"
                Log.e("MainActivity", "Error during startup: ${e.message} | $deviceInfo", e)

        }
    }
}
