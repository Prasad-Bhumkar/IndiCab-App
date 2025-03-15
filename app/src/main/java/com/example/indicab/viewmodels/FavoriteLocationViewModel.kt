package com.example.indicab.viewmodels

import com.example.indicab.services.UserManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.indicab.data.FavoriteLocationRepository
import com.example.indicab.models.FavoriteLocation
import com.example.indicab.models.FavoriteLocationType
import com.example.indicab.models.Location
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class FavoriteLocationViewModel(
    private val repository: FavoriteLocationRepository,
    private val userManager: UserManager
) : ViewModel() {
    private val userId: String
        get() = userManager.getUserId().first() ?: throw IllegalStateException("User ID not available")
    private fun validateLocation(location: Location): Boolean {
        return location.latitude in -90.0..90.0 &&
               location.longitude in -180.0..180.0
    }

    private fun validateLabel(label: String): Boolean {
        return label.isNotBlank()
    }

    private fun handleLocationError(e: Exception): FavoriteLocationUiState.Error {
        e.printStackTrace()
        return when (e) {
            is IllegalArgumentException -> FavoriteLocationUiState.Error(e.message ?: "Invalid input")
            else -> FavoriteLocationUiState.Error("Failed to perform location operation")
        }
    }

    private val cachedLocations = mutableMapOf<String, FavoriteLocation>()

    private fun getCachedLocation(id: String): FavoriteLocation? {
        return cachedLocations[id]
    }

    private fun cacheLocation(location: FavoriteLocation) {
        cachedLocations[location.id] = location
    }

    private val _categories = MutableStateFlow<List<String>>(emptyList())
    val categories = _categories.asStateFlow()

    init {
        viewModelScope.launch {
            _categories.value = repository.getAllCategories(userId)
        }
    }

    fun shareLocation(locationId: String, recipientId: String): Boolean {
        viewModelScope.launch {
            try {
                val location = repository.getFavoriteLocationById(locationId, userId)
                location?.let {
                    val shareLink = createShareLink(it)
                    // TODO: Implement actual sharing logic (e.g., via SMS, email, etc.)
                    return@shareLocation true
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return false
    }

    private fun createShareLink(location: FavoriteLocation): String {
        return "https://maps.indicab.com/location/${location.id}"
    }

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _selectedType = MutableStateFlow<FavoriteLocationType?>(null)
    val selectedType = _selectedType.asStateFlow()

    val favoriteLocations = combine(
        searchQuery,
        selectedType
    ) { query, type ->
        Pair(query, type)
    }.flatMapLatest { (query, type) ->
        when {
            query.isNotBlank() -> repository.searchFavoriteLocations(userId, query)
            type != null -> repository.getFavoriteLocationsByType(userId, type)
            else -> repository.getAllFavoriteLocations(userId)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val recentLocations = repository.getRecentFavoriteLocations(userId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val homeAndWorkLocations = repository.getHomeAndWorkLocations(userId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val availableTags = repository.getAllTags(userId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _uiState = MutableStateFlow<FavoriteLocationUiState>(FavoriteLocationUiState.Initial)
    val uiState = _uiState.asStateFlow()

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedType(type: FavoriteLocationType?) {
        _selectedType.value = type
    }

    fun addFavoriteLocation(
        location: Location,
        label: String,
        type: FavoriteLocationType,
        tags: List<String> = emptyList()
    ) {
        viewModelScope.launch {
            try {
                _uiState.value = FavoriteLocationUiState.Loading
                if (!validateLocation(location)) {
                    throw IllegalArgumentException("Invalid location coordinates")
                }
                if (!validateLabel(label)) {
                    throw IllegalArgumentException("Label cannot be empty")
                }
                val favoriteLocation = FavoriteLocation(
                    userId = userId,
                    location = location,
                    label = label,
                    type = type,
                    tags = tags
                )
                repository.addFavoriteLocation(favoriteLocation)
                cacheLocation(favoriteLocation)
                _uiState.value = FavoriteLocationUiState.Success
            } catch (e: Exception) {
                _uiState.value = handleLocationError(e)
            }
        }
    }

    fun updateFavoriteLocation(
        id: String,
        label: String? = null,
        type: FavoriteLocationType? = null,
        tags: List<String>? = null
    ) {
        viewModelScope.launch {
            try {
                _uiState.value = FavoriteLocationUiState.Loading
                val existing = repository.getFavoriteLocationById(id, userId)
                existing?.let {
                    if (label != null && !validateLabel(label)) {
                        throw IllegalArgumentException("Label cannot be empty")
                    }
                    val updated = it.copy(
                        label = label ?: it.label,
                        type = type ?: it.type,
                        tags = tags ?: it.tags,
                        updatedAt = LocalDateTime.now()
                    )
                    repository.updateFavoriteLocation(updated)
                    cacheLocation(updated)
                    _uiState.value = FavoriteLocationUiState.Success
                } ?: run {
                    _uiState.value = FavoriteLocationUiState.Error("Location not found")
                }
            } catch (e: Exception) {
                _uiState.value = handleLocationError(e)
            }
        }
    }

    fun deleteFavoriteLocation(location: FavoriteLocation) {
        viewModelScope.launch {
            try {
                _uiState.value = FavoriteLocationUiState.Loading
                repository.deleteFavoriteLocation(location)
                _uiState.value = FavoriteLocationUiState.Success
            } catch (e: Exception) {
                _uiState.value = FavoriteLocationUiState.Error(e.message ?: "Failed to delete location")
            }
        }
    }

    fun useFavoriteLocation(id: String) {
        viewModelScope.launch {
            try {
                repository.incrementUsageCount(id, userId)
            } catch (e: Exception) {
                // Silently fail as this is not critical
            }
        }
    }

    class Factory(
        private val repository: FavoriteLocationRepository,
        private val userManager: UserManager
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(FavoriteLocationViewModel::class.java)) {
                return FavoriteLocationViewModel(repository, userManager) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

sealed class FavoriteLocationUiState {
    object Initial : FavoriteLocationUiState()
    object Loading : FavoriteLocationUiState()
    object Success : FavoriteLocationUiState()
    data class Error(val message: String) : FavoriteLocationUiState()
}
