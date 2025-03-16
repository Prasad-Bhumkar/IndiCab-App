package com.example.indicab.viewmodels

import com.example.indicab.api.BookingService
import com.example.indicab.models.BookingRequest
import com.example.indicab.models.CarType
import com.example.indicab.models.FareDetails
import com.example.indicab.models.Location
import com.google.firebase.auth.FirebaseAuth
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private lateinit var viewModel: HomeViewModel
    private val bookingService: BookingService = mockk()
    private val auth: FirebaseAuth = mockk()

    @Before
    fun setUp() {
        viewModel = HomeViewModel(bookingService, auth)
    }

    @Test
    fun `loadCarTypes should update state with car types when successful`() = runTest {
        // Arrange
        val carTypes = listOf(
            CarType(
                id = "1",
                name = "Sedan",
                description = "Comfortable sedan",
                basePrice = 100.0,
                pricePerKm = 12.0,
                capacity = 4,
                imageUrl = "https://example.com/sedan.jpg"
            )
        )
        coEvery { bookingService.getCarTypes() } returns Response.success(carTypes)

        // Act
        viewModel.loadCarTypes()

        // Assert
        val state = viewModel.uiState.value as HomeScreenState.Success
        assertEquals(carTypes, state.availableCarTypes)
    }

    @Test
    fun `calculateFare should update state with fare details`() = runTest {
        // Arrange
        val pickup = Location(0.0, 0.0)
        val drop = Location(0.0, 0.0)
        val carTypeId = "1"
        val expectedFare = FareDetails(
            baseFare = 100.0,
            distanceFare = 0.0,
            totalFare = 100.0,
            distance = 0.0,
            estimatedTime = "30 mins",
            currency = "INR"
        )

        // Act
        viewModel.calculateFare(pickup, drop, carTypeId)

        // Assert
        val state = viewModel.uiState.value as HomeScreenState.Success
        assertEquals(expectedFare, state.fareDetails)
    }

    @Test
    fun `createBooking should update state with booking`() = runTest {
        // Arrange
        val bookingRequest = BookingRequest(
            userId = "123",
            pickupLocation = Location(0.0, 0.0),
            dropLocation = Location(0.0, 0.0),
            carTypeId = "1"
        )
        coEvery { bookingService.createBooking(any()) } returns Response.success(bookingRequest)

        // Act
        viewModel.createBooking(bookingRequest)

        // Assert
        val state = viewModel.uiState.value as HomeScreenState.Success
        assertEquals(bookingRequest, state.bookingCreated)
    }

    @Test
    fun `setPickupLocation should update state`() = runTest {
        // Arrange
        val location = Location(1.0, 1.0)

        // Act
        viewModel.setPickupLocation(location)

        // Assert
        val state = viewModel.uiState.value as HomeScreenState.Success
        assertEquals(location, state.pickupLocation)
    }

    @Test
    fun `setDropLocation should update state`() = runTest {
        // Arrange
        val location = Location(2.0, 2.0)

        // Act
        viewModel.setDropLocation(location)

        // Assert
        val state = viewModel.uiState.value as HomeScreenState.Success
        assertEquals(location, state.dropLocation)
    }
}
