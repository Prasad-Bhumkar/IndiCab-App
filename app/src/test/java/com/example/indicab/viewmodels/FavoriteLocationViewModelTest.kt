package com.example.indicab.viewmodels

import com.example.indicab.data.FavoriteLocationRepository
import com.example.indicab.models.FavoriteLocation
import com.example.indicab.models.Location
import com.example.indicab.services.UserManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*

@OptIn(ExperimentalCoroutinesApi::class)
class FavoriteLocationViewModelTest {

    private lateinit var viewModel: FavoriteLocationViewModel
    private lateinit var repository: FavoriteLocationRepository
    private lateinit var userManager: UserManager

    @Before
    fun setUp() {
        repository = mock(FavoriteLocationRepository::class.java)
        userManager = mock(UserManager::class.java)
        viewModel = FavoriteLocationViewModel(repository, userManager)
    }

    @Test
    fun testAddFavoriteLocation() = runTest {
        val location = Location(0.0, 0.0)
        val label = "Home"
        val type = FavoriteLocationType.HOME

        viewModel.addFavoriteLocation(location, label, type)

        // Verify that the repository's addFavoriteLocation method was called
        verify(repository).addFavoriteLocation(any())
    }

    @Test
    fun testShareLocation() = runTest {
        val locationId = "123"
        val recipientId = "456"

        // Mock repository response
        val location = FavoriteLocation(
            userId = "user123",
            location = Location(0.0, 0.0),
            label = "Home",
            type = FavoriteLocationType.HOME
        )
        `when`(repository.getFavoriteLocationById(locationId, "user123")).thenReturn(location)

        val result = viewModel.shareLocation(locationId, recipientId)

        assertTrue(result)
    }
}
