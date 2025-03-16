package com.example.indicab.utils

object Constants {
    // API Constants
    const val BASE_URL = "https://api.indicab.com/"
    const val API_TIMEOUT = 30L
    const val API_VERSION = "v1"

    // Database Constants
    const val DATABASE_NAME = "indicab_database"
    const val DATABASE_VERSION = 1

    // Shared Preferences
    const val PREFS_NAME = "indicab_preferences"
    const val KEY_USER_ID = "user_id"
    const val KEY_AUTH_TOKEN = "auth_token"
    const val KEY_THEME_MODE = "theme_mode"
    const val KEY_LANGUAGE = "language"

    // Payment Constants
    const val CURRENCY_CODE = "INR"
    const val MIN_WALLET_BALANCE = 100.0
    const val DEFAULT_RELOAD_AMOUNT = 500.0

    // Location Constants
    const val DEFAULT_ZOOM_LEVEL = 15f
    const val MIN_LOCATION_UPDATE_INTERVAL = 5000L // 5 seconds
    const val MIN_LOCATION_UPDATE_DISTANCE = 10f // 10 meters
    const val LOCATION_PERMISSION_REQUEST_CODE = 1001

    // Booking Constants
    const val MAX_STOPS = 5
    const val MIN_BOOKING_INTERVAL = 15 // minutes
    const val CANCELLATION_WINDOW = 300000L // 5 minutes in milliseconds
    const val DRIVER_WAIT_TIME = 300000L // 5 minutes in milliseconds

    // Driver Constants
    const val MIN_DRIVER_RATING = 4.0
    const val MAX_ACTIVE_HOURS = 12
    const val MIN_REST_PERIOD = 6
    const val EARNINGS_UPDATE_INTERVAL = 300000L // 5 minutes

    // UI Constants
    const val ANIMATION_DURATION = 300L
    const val SNACKBAR_DURATION = 3000
    const val MAX_RETRY_ATTEMPTS = 3

    // Notification Channels
    object NotificationChannels {
        const val BOOKING_CHANNEL_ID = "booking_notifications"
        const val PAYMENT_CHANNEL_ID = "payment_notifications"
        const val DRIVER_CHANNEL_ID = "driver_notifications"
        const val PROMOTIONAL_CHANNEL_ID = "promotional_notifications"
        
        const val BOOKING_CHANNEL_NAME = "Booking Updates"
        const val PAYMENT_CHANNEL_NAME = "Payment Updates"
        const val DRIVER_CHANNEL_NAME = "Driver Updates"
        const val PROMOTIONAL_CHANNEL_NAME = "Promotions"
    }

    // Error Messages
    object ErrorMessages {
        const val NO_INTERNET = "No internet connection"
        const val SERVER_ERROR = "Server error occurred"
        const val TIMEOUT_ERROR = "Request timed out"
        const val LOCATION_PERMISSION_DENIED = "Location permission denied"
        const val INVALID_LOCATION = "Invalid location"
        const val PAYMENT_FAILED = "Payment failed"
        const val BOOKING_FAILED = "Booking failed"
        const val INVALID_CARD = "Invalid card details"
        const val INSUFFICIENT_BALANCE = "Insufficient wallet balance"
    }

    // Date Formats
    object DateFormats {
        const val API_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
        const val DISPLAY_DATE_FORMAT = "MMM dd, yyyy"
        const val DISPLAY_TIME_FORMAT = "HH:mm"
        const val DISPLAY_DATE_TIME_FORMAT = "MMM dd, yyyy HH:mm"
    }

    // Regular Expressions
    object Regex {
        const val EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$"
        const val PHONE_PATTERN = "^[0-9]{10}\$"
        const val PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#\$%^&+=])(?=\\S+\$).{8,}\$"
        const val NAME_PATTERN = "^[a-zA-Z ]{2,30}\$"
        const val VEHICLE_NUMBER_PATTERN = "^[A-Z]{2}[0-9]{2}[A-Z]{2}[0-9]{4}\$"
    }
}
