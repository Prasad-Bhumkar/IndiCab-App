package com.example.indicab.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.indicab.monitoring.AppMonitor
import com.example.indicab.screens.ErrorLog
import com.example.indicab.utils.PerformanceTracker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class MonitoringViewModel @Inject constructor(
    private val appMonitor: AppMonitor,
    private val performanceTracker: PerformanceTracker
) : ViewModel() {

    var appHealth by mutableStateOf<AppMonitor.AppHealth?>(null)
        private set

    var performanceMetrics by mutableStateOf<PerformanceTracker.MetricSummary?>(null)
        private set

    var syncStatus by mutableStateOf("")
        private set

    private val _recentErrors = MutableStateFlow<List<ErrorLog>>(emptyList())
    val recentErrors: StateFlow<List<ErrorLog>> = _recentErrors

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        refreshMetrics()
    }

    fun refreshMetrics() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Update app health
                appHealth = appMonitor.checkAppHealth()

                // Update performance metrics
                performanceMetrics = performanceTracker.getMetrics("network_request")

                // Update sync status
                syncStatus = when {
                    appHealth?.isSyncHealthy == true -> "Sync Status: Healthy"
                    else -> "Sync Status: Issues Detected"
                }

                // Simulate fetching recent errors (replace with actual error logging system)
                _recentErrors.value = listOf(
                    ErrorLog(
                        timestamp = Date(),
                        message = "Sample error message",
                        location = "BookingScreen"
                    )
                )
            } catch (e: Exception) {
                _recentErrors.value = listOf(
                    ErrorLog(
                        timestamp = Date(),
                        message = "Failed to refresh metrics: ${e.message}",
                        location = "MonitoringViewModel"
                    )
                )
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearErrorLogs() {
        _recentErrors.value = emptyList()
    }

    fun exportMetrics(): String {
        return buildString {
            append("=== IndiCab Monitoring Report ===\n\n")
            
            append("App Health:\n")
            appHealth?.let {
                append("Network Health: ${if (it.isNetworkHealthy) "Good" else "Poor"}\n")
                append("UI Health: ${if (it.isUiHealthy) "Good" else "Poor"}\n")
                append("Sync Health: ${if (it.isSyncHealthy) "Good" else "Poor"}\n")
                append("Network Latency: ${it.networkLatency}ms\n")
                append("UI Render Time: ${it.uiRenderTime}ms\n")
            }

            append("\nPerformance Metrics:\n")
            performanceMetrics?.let {
                append("Average Response Time: ${it.averageTime}ms\n")
                append("Max Response Time: ${it.maxTime}ms\n")
                append("Min Response Time: ${it.minTime}ms\n")
                append("Total Requests: ${it.count}\n")
            }

            append("\nSync Status: $syncStatus\n")

            append("\nRecent Errors:\n")
            _recentErrors.value.forEach {
                append("${it.timestamp} - ${it.message} (${it.location})\n")
            }
        }
    }

    companion object {
        private const val TAG = "MonitoringViewModel"
    }
}
