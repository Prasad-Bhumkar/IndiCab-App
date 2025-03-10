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
    val selectedCarType: CarType? = null,
    val availableCarTypes: List<CarType> = emptyList(),
    val fareDetails: FareDetails? = null
)

class HomeViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()

    private val _uiState = MutableStateFlow(HomeScreenState())
    val uiState: StateFlow<HomeScreenState> = _uiState

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading
    
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error
    
    private val _carTypes = MutableLiveData<List<CarType>>()
    val carTypes: LiveData<List<CarType>> = _carTypes
    
    private val _fareDetails = MutableLiveData<FareDetails?>()
    val fareDetails: LiveData<FareDetails?> = _fareDetails
    
    private val _bookingCreated = MutableLiveData<BookingRequest?>()
    val bookingCreated: LiveData<BookingRequest?> = _bookingCreated
    
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
        _loading.value = true
        _error.value = null
        
        viewModelScope.launch {
            try {
                val response = bookingService.getCarTypes()
                if (response.success && response.data != null) {
                    _carTypes.value = response.data
                } else {
                    _error.value = "Failed to load car types"
                }
            } catch (e: Exception) {
                _error.value = "An error occurred while loading car types: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }
    
    fun calculateFare(pickupLocation: Location, dropLocation: Location, carTypeId: String) {
        viewModelScope.launch {
            try {
                _loading.value = true
                _error.value = null
                
                // TODO: Implement fare calculation logic with the provided parameters
                // For now, we'll create a dummy fare details
                val selectedCarType = _carTypes.value?.find { it.id == carTypeId }
                
                if (selectedCarType == null) {
                    _error.value = "Selected car type not found"
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
                
                _fareDetails.value = fareDetails
            } catch (e: Exception) {
                _error.value = "An error occurred while calculating fare: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }
    
    fun createBooking(bookingRequest: BookingRequest) {
        viewModelScope.launch {
            try {
                _loading.value = true
                _error.value = null
                
                // TODO: Implement booking creation logic with the provided request
                // For now, we'll just return the booking request
                _bookingCreated.value = bookingRequest
            } catch (e: Exception) {
                _error.value = "An error occurred while creating booking: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun setPickupLocation(location: LatLng) {
        _uiState.value = _uiState.value.copy(pickupLocation = location)
    }

    fun setDropLocation(location: LatLng) {
        _uiState.value = _uiState.value.copy(dropLocation = location)
    }

    fun selectCarType(carType: CarType) {
        _uiState.value = _uiState.value.copy(selectedCarType = carType)
    }
}