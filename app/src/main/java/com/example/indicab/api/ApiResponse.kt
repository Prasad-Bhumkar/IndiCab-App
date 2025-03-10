package com.example.indicab.api

data class ApiResponse<T>(
    val success: Boolean,
    val data: T?,
    val error: String?
) 