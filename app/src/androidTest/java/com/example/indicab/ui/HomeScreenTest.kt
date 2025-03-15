package com.example.indicab.ui

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.indicab.viewmodels.HomeViewModel
import io.mockk.mockk
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()
    private val viewModel: HomeViewModel = mockk()

    @Test
    fun homeScreen_shouldDisplayInitialContent() {
        // Arrange
        composeTestRule.setContent {
            HomeScreen(viewModel)
        }

        // Assert
        composeTestRule.onNodeWithText("Select Car Type").assertExists()
        composeTestRule.onNodeWithText("Enter Pickup Location").assertExists()
        composeTestRule.onNodeWithText("Enter Drop Location").assertExists()
    }

    @Test
    fun selectCarType_shouldUpdateSelection() {
        // Arrange
        composeTestRule.setContent {
            HomeScreen(viewModel)
        }

        // Act
        composeTestRule.onNodeWithText("Select Car Type").performClick()
        composeTestRule.onNodeWithText("Sedan").performClick()

        // Assert
        composeTestRule.onNodeWithText("Selected: Sedan").assertExists()
    }

    @Test
    fun enterLocations_shouldUpdateInputs() {
        // Arrange
        composeTestRule.setContent {
            HomeScreen(viewModel)
        }

        // Act
        composeTestRule.onNodeWithText("Enter Pickup Location")
            .performTextInput("123 Main St")
        composeTestRule.onNodeWithText("Enter Drop Location")
            .performTextInput("456 Elm St")

        // Assert
        composeTestRule.onNodeWithText("123 Main St").assertExists()
        composeTestRule.onNodeWithText("456 Elm St").assertExists()
    }

    @Test
    fun calculateFare_shouldDisplayResult() {
        // Arrange
        composeTestRule.setContent {
            HomeScreen(viewModel)
        }

        // Act
        composeTestRule.onNodeWithText("Calculate Fare").performClick()

        // Assert
        composeTestRule.onNodeWithText("Estimated Fare:").assertExists()
    }

    @Test
    fun createBooking_shouldNavigateToConfirmation() {
        // Arrange
        composeTestRule.setContent {
            HomeScreen(viewModel)
        }

        // Act
        composeTestRule.onNodeWithText("Book Now").performClick()

        // Assert
        composeTestRule.onNodeWithText("Booking Confirmation").assertExists()
    }
}
