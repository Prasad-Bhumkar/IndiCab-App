package com.example.indicab.services

import com.example.indicab.data.repositories.DriverRepository
import com.example.indicab.models.Driver
import com.example.indicab.utils.DriverValidator
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class DriverServiceTest {

    private val mockRepository: DriverRepository = mock()
    private val driverService = DriverService(mockRepository)

    @Test
    fun `getDriver should return driver when exists`() = runTest {
        // Arrange
        val testDriver = Driver(id = "1", name = "John Doe", license = "ABC123")
        whenever(mockRepository.getDriverProfile("1")).thenReturn(flowOf(testDriver))

        // Act
        val result = driverService.getDriver("1")

        // Assert
        result.collect { driver ->
            assertEquals(testDriver, driver)
        }
    }

    @Test
    fun `addDriver should return true for valid driver`() = runTest {
        // Arrange
        val validDriver = Driver(id = "1", name = "John Doe", license = "ABC123")

        // Act
        val result = driverService.addDriver(validDriver)

        // Assert
        assertTrue(result)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `addDriver should throw exception for invalid driver`() = runTest {
        // Arrange
        val invalidDriver = Driver(id = "1", name = "", license = "")

        // Act
        driverService.addDriver(invalidDriver)

        // Assert - Exception expected
    }

    @Test
    fun `getVehiclesForDriver should return vehicles`() = runTest {
        // Arrange
        val testVehicles = listOf(
            Vehicle(id = "1", driverId = "1", make = "Toyota", model = "Corolla"),
            Vehicle(id = "2", driverId = "1", make = "Honda", model = "Civic")
        )
        whenever(mockRepository.getVehicleDetails("1")).thenReturn(flowOf(testVehicles))

        // Act
        val result = driverService.getVehiclesForDriver("1")

        // Assert
        result.collect { vehicles ->
            assertEquals(testVehicles, vehicles)
        }
    }
}
