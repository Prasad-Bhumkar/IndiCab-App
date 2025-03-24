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
            stateHolder.updateLocationPermissionState(isGranted)
            stateHolder.updateActivityState(if (isGranted) ActivityState.PERMISSION_GRANTED else ActivityState.PERMISSION_DENIED)
            lifecycleScope.launch {
                snackbarHostState.showSnackbar(if (isGranted) "Location permission granted." else "Location permission denied. Please enable it in settings.")
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
            NavigationSetup(navController = navController, snackbarHostState = snackbarHostState)
        }
    }
}
