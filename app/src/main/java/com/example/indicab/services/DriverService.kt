package com.example.indicab.services

import com.example.indicab.data.repositories.DriverRepository
import com.example.indicab.models.Driver
import com.example.indicab.models.Vehicle
import com.example.indicab.utils.DriverValidator
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DriverService @Inject constructor(
    private val driverRepository: DriverRepository
) {
    suspend fun getDriver(userId: String): Flow<Driver?> {
        return driverRepository.getDriverProfile(userId)
    }

    suspend fun addDriver(driver: Driver): Boolean {
        return when (val validation = DriverValidator.validateDriver(driver)) {
            is DriverValidator.ValidationResult.Success -> {
                driverRepository.updateDriverProfile(driver.toProfile())
                true
            }
            is DriverValidator.ValidationResult.Error -> {
                throw IllegalArgumentException(validation.message)
            }
        }
    }

    suspend fun getVehiclesForDriver(driverId: String): Flow<List<Vehicle>> {
        return driverRepository.getVehicleDetails(driverId)
    }

    private fun Driver.toProfile(): DriverProfile {
        return DriverProfile(
            id = this.id,
            name = this.name,
            license = this.license,
            // Add other necessary fields
        )
    }
}
