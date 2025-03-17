package com.example.indicab

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.remember
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.lifecycleScope
import com.example.indicab.navigation.NavigationSetup
import com.example.indicab.utils.ErrorLogger
import com.example.indicab.state.ActivityStateHolder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val stateHolder = ActivityStateHolder()
        val snackbarHostState = SnackbarHostState()

        // ActivityResultLauncher for location permission
        val locationPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                stateHolder.updateLocationPermissionState(true)
                stateHolder.updateActivityState(ActivityState.PERMISSION_GRANTED)
                lifecycleScope.launch {
                    snackbarHostState.showSnackbar("Location permission granted.")
                }
            } else {
                stateHolder.updateActivityState(ActivityState.PERMISSION_DENIED)
                lifecycleScope.launch {
                    snackbarHostState.showSnackbar("Location permission denied. Please enable it in settings.")
                }
            }
        }

        // Check and handle location permissions
        lifecycleScope.launch {
            if (!PermissionUtils.checkLocationPermission(this@MainActivity)) {
                stateHolder.updateActivityState(ActivityState.PERMISSION_REQUESTED)
                locationPermissionRequest.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            } else {
                stateHolder.updateLocationPermissionState(true)
                stateHolder.updateActivityState(ActivityState.PERMISSION_GRANTED)
            }
        }

        setContent {
            val navController = rememberNavController()
            val currentStateHolder = remember { stateHolder }
            val currentSnackbarHostState = remember { snackbarHostState }

            NavigationSetup(
                navController = navController,
                snackbarHostState = currentSnackbarHostState
            )
        }
    }
}
