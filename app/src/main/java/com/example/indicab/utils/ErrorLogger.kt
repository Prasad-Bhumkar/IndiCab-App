package com.example.indicab.utils

import android.util.Log

object ErrorLogger {
    private const val TAG = "ErrorLogger"

    fun logError(exception: Exception) {
        Log.e(TAG, "An error occurred: ${exception.message}", exception)
    }
}
