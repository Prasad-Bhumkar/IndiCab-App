package com.example.indicab.utils

import com.example.indicab.models.Driver
import com.example.indicab.data.repositories.DriverRepository

object DriverValidator {
    fun validateDriver(driver: Driver): ValidationResult {
        return when {
            driver.name.isBlank() -> ValidationResult.Error("Driver name cannot be blank")
            driver.license.isBlank() -> ValidationResult.Error("Driver license cannot be blank")
            else -> ValidationResult.Success
        }
    }

    sealed class ValidationResult {
        object Success : ValidationResult()
        data class Error(val message: String) : ValidationResult()
    }
}
