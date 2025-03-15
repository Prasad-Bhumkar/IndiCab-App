package com.example.indicab.services

import android.content.Context
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.perf.FirebasePerformance
import com.google.firebase.perf.ktx.performance
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MonitoringService @Inject constructor(context: Context) {

    private val analytics: FirebaseAnalytics = Firebase.analytics
    private val crashlytics: FirebaseCrashlytics = Firebase.crashlytics
    private val performance: FirebasePerformance = Firebase.performance

    fun trackEvent(eventName: String, params: Map<String, Any?> = emptyMap()) {
        analytics.logEvent(eventName) {
            params.forEach { (key, value) ->
                param(key, value.toString())
            }
        }
    }

    fun setUserProperty(key: String, value: String) {
        analytics.setUserProperty(key, value)
    }

    fun logError(throwable: Throwable, message: String? = null) {
        crashlytics.apply {
            if (message != null) {
                log(message)
            }
            recordException(throwable)
        }
    }

    fun setUserId(userId: String) {
        analytics.setUserId(userId)
        crashlytics.setUserId(userId)
    }

    fun startTrace(name: String) = FirebasePerformance.getInstance().newTrace(name).apply {
        start()
    }

    fun stopTrace(trace: com.google.firebase.perf.metrics.Trace) {
        trace.stop()
    }
}
