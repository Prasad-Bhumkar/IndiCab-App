package com.example.indicab.monitoring

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
    }
}
