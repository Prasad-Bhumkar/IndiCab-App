package com.example.indicab.screens

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.indicab.models.CarType
import com.example.indicab.models.Location
import com.example.indicab.viewmodels.BookRideViewModel
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import java.time.LocalDateTime

class SimpleBookingScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val mockViewModel = mock(BookRideViewModel::class.java)

    @Test
    fun testLocationInputsAreDisplayed() {
        composeTestRule.setContent {
            SimpleBookingScreen(
                onBookingComplete = {},
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithText("Pickup Location")
            .assertExists()
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Drop Location")
            .assertExists()
            .assertIsDisplayed()
    }

    @Test
    fun testBookButtonInitiallyDisabled() {
        composeTestRule.setContent {
            SimpleBookingScreen(
                onBookingComplete = {},
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithText("Book Ride")
            .assertExists()
            .assertIsNotEnabled()
    }

    @Test
    fun testLocationInputsUpdateViewModel() {
        composeTestRule.setContent {
            SimpleBookingScreen(
                onBookingComplete = {},
                viewModel = mockViewModel
            )
        }

        // Enter pickup location
        composeTestRule
            .onNodeWithText("Pickup Location")
            .performTextInput("Test Pickup")

        // Enter drop location
        composeTestRule
            .onNodeWithText("Drop Location")
            .performTextInput("Test Drop")

        // Verify view model updates
        verify(mockViewModel).setPickupLocation(any())
        verify(mockViewModel).setDropLocation(any())
    }

    @Test
    fun testCarTypeSelectionDisplayed() {
        val testCarTypes = listOf(
            CarType(1, "Sedan", "Comfortable sedan", 100.0, "car_image.png"),
            CarType(2, "SUV", "Spacious SUV", 150.0, "suv_image.png")
        )

        composeTestRule.setContent {
            SimpleBookingScreen(
                onBookingComplete = {},
                viewModel = mockViewModel,
                availableCarTypes = testCarTypes
            )
        }

        composeTestRule
            .onNodeWithText("Sedan")
            .assertExists()
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("SUV")
            .assertExists()
            .assertIsDisplayed()
    }

    @Test
    fun testDateTimePickerDisplayed() {
        composeTestRule.setContent {
            SimpleBookingScreen(
                onBookingComplete = {},
                viewModel = mockViewModel
            )
        }

        // Click on date/time selector
        composeTestRule
            .onNodeWithText("Select Date & Time")
            .performClick()

        // Verify date picker dialog is shown
        composeTestRule
            .onNodeWithTag("DateTimePicker")
            .assertExists()
            .assertIsDisplayed()
    }

    @Test
    fun testCompleteBookingFlow() {
        composeTestRule.setContent {
            SimpleBookingScreen(
                onBookingComplete = {},
                viewModel = mockViewModel
            )
        }

        // Enter locations
        composeTestRule
            .onNodeWithText("Pickup Location")
            .performTextInput("Test Pickup")

        composeTestRule
            .onNodeWithText("Drop Location")
            .performTextInput("Test Drop")

        // Select car type
        composeTestRule
            .onNodeWithText("Sedan")
            .performClick()

        // Select date and time
        composeTestRule
            .onNodeWithText("Select Date & Time")
            .performClick()

        composeTestRule
            .onNodeWithTag("ConfirmDateTime")
            .performClick()

        // Verify book button is enabled and click it
        composeTestRule
            .onNodeWithText("Book Ride")
            .assertIsEnabled()
            .performClick()

        // Verify booking attempt
        verify(mockViewModel).bookRide()
    }

    private fun <T> any(): T {
        return org.mockito.kotlin.any()
    }
}
