package com.example.indicab.viewmodels

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
    private val userId: String // TODO: Get from UserManager
) : ViewModel() {

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
                val favoriteLocation = FavoriteLocation(
                    userId = userId,
                    location = location,
                    label = label,
                    type = type,
                    tags = tags
                )
                repository.addFavoriteLocation(favoriteLocation)
                _uiState.value = FavoriteLocationUiState.Success
            } catch (e: Exception) {
                _uiState.value = FavoriteLocationUiState.Error(e.message ?: "Failed to add location")
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
                    val updated = it.copy(
                        label = label ?: it.label,
                        type = type ?: it.type,
                        tags = tags ?: it.tags,
                        updatedAt = LocalDateTime.now()
                    )
                    repository.updateFavoriteLocation(updated)
                    _uiState.value = FavoriteLocationUiState.Success
                } ?: run {
                    _uiState.value = FavoriteLocationUiState.Error("Location not found")
                }
            } catch (e: Exception) {
                _uiState.value = FavoriteLocationUiState.Error(e.message ?: "Failed to update location")
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
        private val userId: String
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(FavoriteLocationViewModel::class.java)) {
                return FavoriteLocationViewModel(repository, userId) as T
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
