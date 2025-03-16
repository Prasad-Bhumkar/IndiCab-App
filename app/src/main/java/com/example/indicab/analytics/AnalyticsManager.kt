package com.example.indicab.analytics

import android.content.Context
import android.os.Bundle
import com.example.indicab.BuildConfig
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnalyticsManager @Inject constructor(
    context: Context
) {
    private val firebaseAnalytics = FirebaseAnalytics.getInstance(context)
    private val crashlytics = FirebaseCrashlytics.getInstance()

    init {
        crashlytics.setCrashlyticsCollectionEnabled(!BuildConfig.DEBUG)
        setDefaultProperties()
    }

    private fun setDefaultProperties() {
        crashlytics.setCustomKey("app_version", BuildConfig.VERSION_NAME)
        crashlytics.setCustomKey("build_type", BuildConfig.BUILD_TYPE)
    }

    fun logEvent(event: AnalyticsEvent) {
        val bundle = Bundle().apply {
            event.parameters.forEach { (key, value) ->
                when (value) {
                    is String -> putString(key, value)
                    is Int -> putInt(key, value)
                    is Long -> putLong(key, value)
                    is Float -> putFloat(key, value)
                    is Double -> putDouble(key, value)
                    is Boolean -> putBoolean(key, value)
                }
            }
        }
        firebaseAnalytics.logEvent(event.name, bundle)
    }

    fun setUserProperty(property: UserProperty) {
        firebaseAnalytics.setUserProperty(property.key, property.value)
        crashlytics.setCustomKey(property.key, property.value)
    }

    fun logError(throwable: Throwable, customAttributes: Map<String, String> = emptyMap()) {
        customAttributes.forEach { (key, value) ->
            crashlytics.setCustomKey(key, value)
        }
        crashlytics.recordException(throwable)
    }

    fun startScreen(screenName: String, screenClass: String) {
        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
            putString(FirebaseAnalytics.Param.SCREEN_CLASS, screenClass)
        }
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
    }

    companion object {
        private const val TAG = "AnalyticsManager"
    }
}

sealed class AnalyticsEvent(
    val name: String,
    val parameters: Map<String, Any> = emptyMap()
) {
    // Booking Events
    data class BookingStarted(
        val pickupLocation: String,
        val dropLocation: String,
        val carType: String
    ) : AnalyticsEvent(
        name = "booking_started",
        parameters = mapOf(
            "pickup_location" to pickupLocation,
            "drop_location" to dropLocation,
            "car_type" to carType
        )
    )

    data class BookingCompleted(
        val bookingId: String,
        val amount: Double,
        val paymentMethod: String
    ) : AnalyticsEvent(
        name = "booking_completed",
        parameters = mapOf(
            "booking_id" to bookingId,
            "amount" to amount,
            "payment_method" to paymentMethod
        )
    )

    data class BookingCancelled(
        val bookingId: String,
        val reason: String
    ) : AnalyticsEvent(
        name = "booking_cancelled",
        parameters = mapOf(
            "booking_id" to bookingId,
            "reason" to reason
        )
    )

    // Payment Events
    data class PaymentInitiated(
        val amount: Double,
        val paymentMethod: String
    ) : AnalyticsEvent(
        name = "payment_initiated",
        parameters = mapOf(
            "amount" to amount,
            "payment_method" to paymentMethod
        )
    )

    data class PaymentCompleted(
        val amount: Double,
        val paymentMethod: String,
        val status: String
    ) : AnalyticsEvent(
        name = "payment_completed",
        parameters = mapOf(
            "amount" to amount,
            "payment_method" to paymentMethod,
            "status" to status
        )
    )

    // Error Events
    data class ErrorOccurred(
        val errorType: String,
        val errorMessage: String,
        val screen: String
    ) : AnalyticsEvent(
        name = "error_occurred",
        parameters = mapOf(
            "error_type" to errorType,
            "error_message" to errorMessage,
            "screen" to screen
        )
    )
}

data class UserProperty(
    val key: String,
    val value: String
) {
    companion object {
        fun userType(type: String) = UserProperty("user_type", type)
        fun preferredPaymentMethod(method: String) = UserProperty("preferred_payment_method", method)
        fun cityLocation(city: String) = UserProperty("city", city)
    }
}
