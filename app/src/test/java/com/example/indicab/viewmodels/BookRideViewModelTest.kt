package com.example.indicab.viewmodels

import app.cash.turbine.test
import com.example.indicab.api.BookingService
import com.example.indicab.models.BookingRequest
import com.example.indicab.models.CarType
import com.example.indicab.models.Location
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import retrofit2.Response
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class BookRideViewModelTest {
    
    @Mock
    private lateinit var bookingService: BookingService
    
    private lateinit var viewModel: BookRideViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        viewModel = BookRideViewModel(bookingService)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is Initial`() = runTest {
        viewModel.uiState.test {
            assertTrue(awaitItem() is BookRideUiState.Initial)
        }
    }

    @Test
    fun `setting locations updates state`() = runTest {
        val pickup = Location(1.0, 1.0, "Pickup")
        val drop = Location(2.0, 2.0, "Drop")
        
        viewModel.setPickupLocation(pickup)
        viewModel.setDropLocation(drop)
        
        viewModel.uiState.test {
            assertTrue(awaitItem() is BookRideUiState.Initial)
        }
    }

    @Test
    fun `booking ride with valid data succeeds`() = runTest {
        // Setup test data
        val pickup = Location(1.0, 1.0, "Pickup")
        val drop = Location(2.0, 2.0, "Drop")
        val dateTime = LocalDateTime.now()
        val carType = CarType(1, "Sedan", "Comfortable sedan", 100.0, "car_image.png")
        
        val bookingRequest = BookingRequest(
            pickupLocation = pickup,
            dropLocation = drop,
            dateTime = dateTime,
            carType = carType
        )

        // Mock service response
        `when`(bookingService.createBooking(any())).thenReturn(
            Response.success(bookingRequest)
        )

        // Set required data
        viewModel.setPickupLocation(pickup)
        viewModel.setDropLocation(drop)
        viewModel.setDateTime(dateTime)
        viewModel.setCarType(carType)

        // Attempt booking
        viewModel.bookRide()

        // Verify state transitions
        viewModel.uiState.test {
            assertTrue(awaitItem() is BookRideUiState.ReadyToBook)
            assertTrue(awaitItem() is BookRideUiState.Loading)
            assertTrue(awaitItem() is BookRideUiState.Success)
        }
    }

    @Test
    fun `booking ride with invalid data stays in initial state`() = runTest {
        viewModel.bookRide()
        
        viewModel.uiState.test {
            assertTrue(awaitItem() is BookRideUiState.Initial)
        }
    }

    @Test
    fun `booking ride handles error`() = runTest {
        // Setup test data
        val pickup = Location(1.0, 1.0, "Pickup")
        val drop = Location(2.0, 2.0, "Drop")
        val dateTime = LocalDateTime.now()
        val carType = CarType(1, "Sedan", "Comfortable sedan", 100.0, "car_image.png")

        // Mock service error
        `when`(bookingService.createBooking(any())).thenThrow(RuntimeException("Network error"))

        // Set required data
        viewModel.setPickupLocation(pickup)
        viewModel.setDropLocation(drop)
        viewModel.setDateTime(dateTime)
        viewModel.setCarType(carType)

        // Attempt booking
        viewModel.bookRide()

        // Verify error state
        viewModel.uiState.test {
            assertTrue(awaitItem() is BookRideUiState.ReadyToBook)
            assertTrue(awaitItem() is BookRideUiState.Loading)
            val errorState = awaitItem() as BookRideUiState.Error
            assertEquals("Network error", errorState.message)
        }
    }
}
