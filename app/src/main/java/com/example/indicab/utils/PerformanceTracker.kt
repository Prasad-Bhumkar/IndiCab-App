package com.example.indicab.utils

import android.os.SystemClock
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap
import kotlin.system.measureTimeMillis

object PerformanceTracker {
    private const val TAG = "PerformanceTracker"
    private val metrics = ConcurrentHashMap<String, MetricData>()
    private val thresholds = ConcurrentHashMap<String, Long>()

    data class MetricData(
        var totalTime: Long = 0L,
        var count: Int = 0,
        var maxTime: Long = 0L,
        var minTime: Long = Long.MAX_VALUE,
        var lastTimestamp: Long = 0L
    )

    init {
        // Default thresholds in milliseconds
        setThreshold("network_request", 3000)
        setThreshold("database_operation", 500)
        setThreshold("ui_render", 16) // Target 60 FPS
        setThreshold("location_update", 1000)
    }

    fun startMetric(name: String) {
        metrics.computeIfAbsent(name) { MetricData() }.apply {
            lastTimestamp = SystemClock.elapsedRealtime()
        }
    }

    fun endMetric(name: String) {
        metrics[name]?.let { data ->
            val duration = SystemClock.elapsedRealtime() - data.lastTimestamp
            data.totalTime += duration
            data.count++
            data.maxTime = maxOf(data.maxTime, duration)
            data.minTime = minOf(data.minTime, duration)

            // Check threshold
            thresholds[name]?.let { threshold ->
                if (duration > threshold) {
                    Log.w(TAG, "Performance warning: $name took ${duration}ms (threshold: ${threshold}ms)")
                }
            }
        }
    }

    fun measureOperation(name: String, operation: () -> Unit) {
        startMetric(name)
        operation()
        endMetric(name)
    }

    suspend fun measureSuspendOperation(name: String, operation: suspend () -> Unit) {
        startMetric(name)
        operation()
        endMetric(name)
    }

    fun getMetrics(name: String): MetricSummary? {
        return metrics[name]?.let { data ->
            MetricSummary(
                averageTime = if (data.count > 0) data.totalTime / data.count else 0,
                maxTime = data.maxTime,
                minTime = data.minTime,
                count = data.count
            )
        }
    }

    fun setThreshold(name: String, thresholdMs: Long) {
        thresholds[name] = thresholdMs
    }

    fun reset() {
        metrics.clear()
    }

    fun logMetrics() {
        metrics.forEach { (name, data) ->
            val avg = if (data.count > 0) data.totalTime / data.count else 0
            Log.i(TAG, """
                Metric: $name
                Average: ${avg}ms
                Max: ${data.maxTime}ms
                Min: ${data.minTime}ms
                Count: ${data.count}
            """.trimIndent())
        }
    }

    data class MetricSummary(
        val averageTime: Long,
        val maxTime: Long,
        val minTime: Long,
        val count: Int
    )
}

@Composable
fun RememberPerformanceTracking(key: String) {
    val startTime = remember { SystemClock.elapsedRealtime() }

    DisposableEffect(key) {
        onDispose {
            val renderTime = SystemClock.elapsedRealtime() - startTime
            PerformanceTracker.measureOperation("compose_render_$key") {}
            if (renderTime > 16) { // Frame drop threshold (60 FPS)
                Log.w("PerformanceTracker", "Slow render detected for $key: ${renderTime}ms")
            }
        }
    }
}

class PerformanceInterceptor {
    private val scope = CoroutineScope(Dispatchers.Default)

    fun trackNetworkCall(url: String, block: () -> Unit) {
        val metricName = "network_${url.substringAfterLast('/')}"
        val time = measureTimeMillis {
            block()
        }
        
        scope.launch {
            PerformanceTracker.measureOperation(metricName) {}
            if (time > 3000) { // 3 seconds threshold
                Log.w("PerformanceTracker", "Slow network call to $url: ${time}ms")
            }
        }
    }

    fun trackDatabaseOperation(operation: String, block: () -> Unit) {
        val metricName = "database_$operation"
        val time = measureTimeMillis {
            block()
        }
        
        scope.launch {
            PerformanceTracker.measureOperation(metricName) {}
            if (time > 500) { // 500ms threshold
                Log.w("PerformanceTracker", "Slow database operation $operation: ${time}ms")
            }
        }
    }
}

// Extension function for easy tracking
inline fun <T> measurePerformance(name: String, block: () -> T): T {
    PerformanceTracker.startMetric(name)
    try {
        return block()
    } finally {
        PerformanceTracker.endMetric(name)
    }
}
