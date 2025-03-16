vpackage com.example.indicab.monitoring

import android.util.Log
import com.example.indicab.analytics.AnalyticsEvent
import com.example.indicab.analytics.AnalyticsManager
import com.example.indicab.data.sync.SyncManager
import com.example.indicab.utils.ErrorHandler
import com.example.indicab.utils.PerformanceTracker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppMonitor @Inject constructor(
    private val analyticsManager: AnalyticsManager,
    private val errorHandler: ErrorHandler,
    private val performanceTracker: PerformanceTracker,
    private val syncManager: SyncManager
) {
    private val monitoringScope = CoroutineScope(Dispatchers.IO)

    init {
        startMonitoring()
    }

    private fun startMonitoring() {
        monitorSyncState()
        monitorPerformance()
        setupErrorTracking()
        monitorMemoryUsage()
        monitorBatteryImpact()
        monitorBackgroundServices()
    }

    private fun monitorMemoryUsage() {
        monitoringScope.launch {
            while (true) {
                val memoryInfo = ActivityManager.MemoryInfo()
                (context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager)
                    .getMemoryInfo(memoryInfo)
                
                val usedMemory = memoryInfo.totalMem - memoryInfo.availMem
                val memoryPercentage = (usedMemory.toFloat() / memoryInfo.totalMem.toFloat()) * 100
                
                if (memoryPercentage > MEMORY_THRESHOLD_PERCENT) {
                    analyticsManager.logEvent(
                        AnalyticsEvent.ErrorOccurred(
                            errorType = "high_memory_usage",
                            errorMessage = "Memory usage at ${"%.2f".format(memoryPercentage)}%",
                            screen = "memory"
                        )
                    )
                    Log.w(TAG, "High memory usage detected: ${"%.2f".format(memoryPercentage)}%")
                }
                
                delay(MEMORY_MONITOR_INTERVAL)
            }
        }
    }

    private fun monitorBatteryImpact() {
        monitoringScope.launch {
            val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
            val batteryUsage = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW)
            
            if (batteryUsage > BATTERY_THRESHOLD_MA) {
                analyticsManager.logEvent(
                    AnalyticsEvent.ErrorOccurred(
                        errorType = "high_battery_usage",
                        errorMessage = "Battery usage at ${batteryUsage}mA",
                        screen = "battery"
                    )
                )
                Log.w(TAG, "High battery usage detected: ${batteryUsage}mA")
            }
        }
    }

    private fun monitorBackgroundServices() {
        monitoringScope.launch {
            val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val runningServices = activityManager.getRunningServices(Integer.MAX_VALUE)
            
            runningServices.filter { it.service.className.contains("com.example.indicab") }
                .forEach { service ->
                    val serviceDuration = System.currentTimeMillis() - service.lastActivityTime
                    if (serviceDuration > BACKGROUND_SERVICE_THRESHOLD_MS) {
                        analyticsManager.logEvent(
                            AnalyticsEvent.ErrorOccurred(
                                errorType = "long_running_service",
                                errorMessage = "Service ${service.service.className} running for ${serviceDuration}ms",
                                screen = "background"
                            )
                        )
                        Log.w(TAG, "Long running service detected: ${service.service.className} running for ${serviceDuration}ms")
                    }
                }
        }
    }
    private fun monitorSyncState() {
        monitoringScope.launch {
            syncManager.syncState.collectLatest { state ->
                when (state) {
                    is SyncManager.SyncState.Error -> {
                        analyticsManager.logEvent(
                            AnalyticsEvent.ErrorOccurred(
                                errorType = "sync_error",
                                errorMessage = state.message,
                                screen = "background_sync"
                            )
                        )
                        Log.e(TAG, "Sync error: ${state.message}")
                    }
                    is SyncManager.SyncState.Success -> {
                        Log.i(TAG, "Sync completed successfully")
                    }
                    is SyncManager.SyncState.Syncing -> {
                        Log.d(TAG, "Sync in progress")
                    }
                    else -> {}
                }
            }
        }
    }

    private fun monitorPerformance() {
        monitoringScope.launch {
            performanceTracker.getMetrics("network_request")?.let { metrics ->
                if (metrics.averageTime > NETWORK_THRESHOLD_MS) {
                    analyticsManager.logEvent(
                        AnalyticsEvent.ErrorOccurred(
                            errorType = "performance_warning",
                            errorMessage = "Slow network requests detected: ${metrics.averageTime}ms",
                            screen = "network"
                        )
                    )
                    Log.w(TAG, "Slow network requests detected: ${metrics.averageTime}ms")
                }
            }

            performanceTracker.getMetrics("ui_render")?.let { metrics ->
                if (metrics.averageTime > UI_RENDER_THRESHOLD_MS) {
                    analyticsManager.logEvent(
                        AnalyticsEvent.ErrorOccurred(
                            errorType = "performance_warning",
                            errorMessage = "Slow UI rendering detected: ${metrics.averageTime}ms",
                            screen = "ui"
                        )
                    )
                    Log.w(TAG, "Slow UI rendering detected: ${metrics.averageTime}ms")
                }
            }
        }
    }

    private fun setupErrorTracking() {
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            val fullStackTrace = android.util.Log.getStackTraceString(throwable)
            analyticsManager.logError(throwable, mapOf(
                "thread" to thread.name,
                "thread_id" to thread.id.toString(),
                "stacktrace" to fullStackTrace
            ))
            Log.e(TAG, "Uncaught exception in thread ${thread.name} (ID: ${thread.id}): $fullStackTrace")
        }
    }

    fun trackScreenView(screenName: String, screenClass: String) {
        analyticsManager.startScreen(screenName, screenClass)
        performanceTracker.startMetric("screen_view_$screenName")
    }

    fun trackUserAction(action: String, parameters: Map<String, Any> = emptyMap()) {
        analyticsManager.logEvent(
            AnalyticsEvent.CustomEvent(
                action,
                parameters
            )
        )
    }

    fun reportError(error: Throwable, screen: String) {
        analyticsManager.logError(error, mapOf("screen" to screen))
        Log.e(TAG, "Error in $screen: ${error.message}", error)
        Log.e(TAG, "Full stack trace: ${Log.getStackTraceString(error)}")
    }

    fun trackNetworkCall(url: String, durationMs: Long) {
        performanceTracker.measureOperation("network_$url") {
            if (durationMs > NETWORK_THRESHOLD_MS) {
                analyticsManager.logEvent(
                    AnalyticsEvent.ErrorOccurred(
                        errorType = "slow_network",
                        errorMessage = "Network call to $url took ${durationMs}ms",
                        screen = "network"
                    )
                )
                Log.w(TAG, "Network call to $url took ${durationMs}ms")
            }
        }
    }

    fun checkAppHealth(): AppHealth {
        val networkMetrics = performanceTracker.getMetrics("network_request")
        val uiMetrics = performanceTracker.getMetrics("ui_render")
        val syncState = syncManager.syncState.value

        return AppHealth(
            isNetworkHealthy = networkMetrics?.averageTime ?: 0 < NETWORK_THRESHOLD_MS,
            isUiHealthy = uiMetrics?.averageTime ?: 0 < UI_RENDER_THRESHOLD_MS,
            isSyncHealthy = syncState !is SyncManager.SyncState.Error,
            networkLatency = networkMetrics?.averageTime ?: 0,
            uiRenderTime = uiMetrics?.averageTime ?: 0,
            syncStatus = syncState.toString()
        )
    }

    data class AppHealth(
        val isNetworkHealthy: Boolean,
        val isUiHealthy: Boolean,
        val isSyncHealthy: Boolean,
        val networkLatency: Long,
        val uiRenderTime: Long,
        val syncStatus: String
    )

    companion object {
        private const val TAG = "AppMonitor"
        private const val NETWORK_THRESHOLD_MS = 3000L
        private const val UI_RENDER_THRESHOLD_MS = 16L
        private const val MEMORY_THRESHOLD_PERCENT = 80f
        private const val MEMORY_MONITOR_INTERVAL = 60000L
        private const val BATTERY_THRESHOLD_MA = 500
        private const val BACKGROUND_SERVICE_THRESHOLD_MS = 600000L
    }
}
