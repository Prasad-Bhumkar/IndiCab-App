package com.example.indicab.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.indicab.api.ApiResponse
import com.example.indicab.models.BookingRequest
import com.example.indicab.models.CarType
import com.example.indicab.models.FareDetails
import com.example.indicab.models.Location
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class HomeScreenState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val pickupLocation: LatLng? = null,
    val dropLocation: LatLng? = null,
    val pickupAddress: String = "",
    val dropAddress: String = "",
    val pickupQuery: String = "",
    val dropQuery: String = "",
    val selectedCarType: CarType? = null,
    val availableCarTypes: List<CarType> = emptyList(),
    val fareDetails: FareDetails? = null,
    val bookingCreated: BookingRequest? = null,
    val showLocationSuggestions: Boolean = false
)

class HomeViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()

    private val _uiState = MutableStateFlow(HomeScreenState())
    val uiState: StateFlow<HomeScreenState> = _uiState
    
    // Mock implementation of BookingService
    private val bookingService = object {
        fun getCarTypes(): ApiResponse<List<CarType>> {
            // Mock car types
            val carTypes = listOf(
                CarType(
                    id = "1",
                    name = "Sedan",
                    description = "Comfortable sedan for up to 4 passengers",
                    basePrice = 100.0,
                    pricePerKm = 12.0,
                    capacity = 4,
                    imageUrl = "https://example.com/sedan.jpg"
                ),
                CarType(
                    id = "2",
                    name = "SUV",
                    description = "Spacious SUV for up to 6 passengers",
                    basePrice = 150.0,
                    pricePerKm = 15.0,
                    capacity = 6,
                    imageUrl = "https://example.com/suv.jpg"
                ),
                CarType(
                    id = "3",
                    name = "Auto-rickshaw",
                    description = "Budget-friendly auto for up to 3 passengers",
                    basePrice = 50.0,
                    pricePerKm = 8.0,
                    capacity = 3,
                    imageUrl = "https://example.com/auto.jpg"
                )
            )
            return ApiResponse(true, carTypes, null)
        }
    }
    
    fun loadCarTypes() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)
                val response = bookingService.getCarTypes()
                if (response.success && response.data != null) {
                    _uiState.value = _uiState.value.copy(
                        availableCarTypes = response.data,
                        isLoading = false
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        error = "Failed to load car types",
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "An error occurred while loading car types: ${e.message}",
                    isLoading = false
                )
            }
        }
    }
    
    fun calculateFare(pickupLocation: Location, dropLocation: Location, carTypeId: String) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)
                
                val selectedCarType = _uiState.value.availableCarTypes.find { it.id == carTypeId }
                
                if (selectedCarType == null) {
                    _uiState.value = _uiState.value.copy(
                        error = "Selected car type not found",
                        isLoading = false
                    )
                    return@launch
                }
                
                val fareDetails = FareDetails(
                    baseFare = selectedCarType.basePrice,
                    distanceFare = 0.0, // Calculate based on distance
                    totalFare = selectedCarType.basePrice, // Add distance fare
                    distance = 0.0, // Calculate distance between points
                    estimatedTime = "30 mins", // Calculate estimated time
                    currency = "INR"
                )
                
                _uiState.value = _uiState.value.copy(
                    fareDetails = fareDetails,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "An error occurred while calculating fare: ${e.message}",
                    isLoading = false
                )
            }
        }
    }
    
    fun createBooking(bookingRequest: BookingRequest) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)
                
                // TODO: Implement booking creation logic
                // Mock successful booking creation
                _uiState.value = _uiState.value.copy(
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "An error occurred while creating booking: ${e.message}",
                    isLoading = false
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun updatePickupQuery(query: String) {
        _uiState.value = _uiState.value.copy(
            pickupQuery = query,
            showLocationSuggestions = query.length >= 2
        )
    }

    fun updateDropQuery(query: String) {
        _uiState.value = _uiState.value.copy(
            dropQuery = query,
            showLocationSuggestions = query.length >= 2
        )
    }

    fun hideLocationSuggestions() {
        _uiState.value = _uiState.value.copy(showLocationSuggestions = false)
    }

    fun setPickupLocation(location: LatLng, address: String) {
        _uiState.value = _uiState.value.copy(
            pickupLocation = location,
            pickupAddress = address
        )
        loadCarTypesIfLocationsSet()
    }

    fun setDropLocation(location: LatLng, address: String) {
        _uiState.value = _uiState.value.copy(
            dropLocation = location,
            dropAddress = address
        )
        loadCarTypesIfLocationsSet()
    }

    private fun loadCarTypesIfLocationsSet() {
        if (_uiState.value.pickupLocation != null && _uiState.value.dropLocation != null) {
            loadCarTypes()
        }
    }

    fun selectCarType(carType: CarType) {
        _uiState.value = _uiState.value.copy(selectedCarType = carType)
        // Trigger fare calculation if both locations are set
        if (_uiState.value.pickupLocation != null && _uiState.value.dropLocation != null) {
            calculateFare(
                Location(_uiState.value.pickupLocation!!, _uiState.value.pickupAddress),
                Location(_uiState.value.dropLocation!!, _uiState.value.dropAddress),
                carType.id
            )
        }
    }

    fun resetBooking() {
        _uiState.value = _uiState.value.copy(
            bookingCreated = null,
            fareDetails = null,
            selectedCarType = null,
            pickupLocation = null,
            dropLocation = null,
            pickupAddress = "",
            dropAddress = "",
            pickupQuery = "",
            dropQuery = "",
            showLocationSuggestions = false
        )
    }
}