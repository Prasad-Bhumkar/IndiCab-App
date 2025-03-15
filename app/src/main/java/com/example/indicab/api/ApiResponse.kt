package com.example.indicab.api

data class ApiResponse<T>(
    val success: Boolean,
    val data: T?,
    val error: String? = null,
    val errorMessage: String? = null // New property for detailed error messages
) {
    fun isSuccessful(): Boolean {
        return success && error == null
    }
}
