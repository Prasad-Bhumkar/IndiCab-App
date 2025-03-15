package com.example.indicab.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.indicab.api.BookingService
import com.example.indicab.models.BookingRequest
import com.example.indicab.models.CarType
import com.example.indicab.models.FareDetails
import com.example.indicab.models.Location
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface HomeScreenState {
    data object Loading : HomeScreenState
    data class Success(
        val pickupLocation: LatLng? = null,
        val dropLocation: LatLng? = null,
        val selectedCarType: CarType? = null,
        val availableCarTypes: List<CarType> = emptyList(),
        val fareDetails: FareDetails? = null
    ) : HomeScreenState
    data class Error(val message: String) : HomeScreenState
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val bookingService: BookingService,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeScreenState>(HomeScreenState.Loading)
    val uiState: StateFlow<HomeScreenState> = _uiState
    
    fun loadCarTypes() {
        _uiState.value = HomeScreenState.Loading
        
        viewModelScope.launch {
            try {
                // TODO: Implement caching for car types data
                // TODO: Add refresh mechanism for car types
                val response = bookingService.getCarTypes()
                if (response.isSuccessful && response.body() != null) {
                    _uiState.update { currentState ->
                        when (currentState) {
                            is HomeScreenState.Success -> currentState.copy(
                                availableCarTypes = response.body()!!
                            )
                            else -> HomeScreenState.Success(
                                availableCarTypes = response.body()!!
                            )
                        }
                    }
                } else {
                    _uiState.value = HomeScreenState.Error("Failed to load car types")
                }
            } catch (e: Exception) {
                _uiState.value = HomeScreenState.Error(
                    "An error occurred while loading car types: ${e.message}"
                )
            }
        }
    }
    
    fun calculateFare(pickupLocation: Location, dropLocation: Location, carTypeId: String) {
        viewModelScope.launch {
            try {
                _uiState.value = HomeScreenState.Loading
                
                val currentState = _uiState.value as? HomeScreenState.Success
                val selectedCarType = currentState?.availableCarTypes?.find { it.id == carTypeId }
                
                if (selectedCarType == null) {
                    _uiState.value = HomeScreenState.Error("Selected car type not found")
                    return@launch
                }
                
                // TODO: Implement accurate distance calculation using Maps API
                // TODO: Implement dynamic pricing based on traffic conditions
                // TODO: Add surge pricing logic during peak hours
                val fareDetails = FareDetails(
                    baseFare = selectedCarType.basePrice,
                    distanceFare = 0.0, // Calculate based on distance
                    totalFare = selectedCarType.basePrice, // Add distance fare
                    distance = 0.0, // Calculate distance between points
                    estimatedTime = "30 mins", // Calculate estimated time
                    currency = "INR"
                )
                
                _uiState.update { 
                    (it as? HomeScreenState.Success)?.copy(fareDetails = fareDetails) 
                        ?: HomeScreenState.Success(fareDetails = fareDetails)
                }
            } catch (e: Exception) {
                _uiState.value = HomeScreenState.Error(
                    "An error occurred while calculating fare: ${e.message}"
                )
            }
        }
    }
    
    fun createBooking(bookingRequest: BookingRequest) {
        viewModelScope.launch {
            try {
                _uiState.value = HomeScreenState.Loading
                
                // TODO: Implement booking creation logic with the provided request
                // TODO: Add validation for booking request parameters
                // TODO: Handle payment processing
                // TODO: Send booking confirmation notification
                // TODO: Implement error handling for failed bookings
                // For now, we'll just return the booking request
                _uiState.update {
                    (it as? HomeScreenState.Success)?.copy() 
                        ?: HomeScreenState.Success()
                }
            } catch (e: Exception) {
                _uiState.value = HomeScreenState.Error(
                    "An error occurred while creating booking: ${e.message}"
                )
            }
        }
    }

    fun setPickupLocation(location: LatLng) {
        _uiState.update {
            (it as? HomeScreenState.Success)?.copy(pickupLocation = location) 
                ?: HomeScreenState.Success(pickupLocation = location)
        }
    }

    fun setDropLocation(location: LatLng) {
        _uiState.update {
            (it as? HomeScreenState.Success)?.copy(dropLocation = location) 
                ?: HomeScreenState.Success(dropLocation = location)
        }
    }

    fun selectCarType(carType: CarType) {
        _uiState.update {
            (it as? HomeScreenState.Success)?.copy(selectedCarType = carType) 
                ?: HomeScreenState.Success(selectedCarType = carType)
        }
    }
}
