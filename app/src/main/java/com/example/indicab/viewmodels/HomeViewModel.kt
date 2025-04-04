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

<<<<<<< HEAD
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
=======
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
>>>>>>> 81ec31f166cdb0573d5c5135fcdecb0f6ba49d83

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val bookingService: BookingService,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeScreenState>(HomeScreenState.Loading)
    val uiState: StateFlow<HomeScreenState> = _uiState
<<<<<<< HEAD
    
    fun loadCarTypes() {
        _uiState.value = HomeScreenState.Loading
        
=======
    
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
>>>>>>> 81ec31f166cdb0573d5c5135fcdecb0f6ba49d83
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)
                val response = bookingService.getCarTypes()
<<<<<<< HEAD
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
=======
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
>>>>>>> 81ec31f166cdb0573d5c5135fcdecb0f6ba49d83
                )
            }
        }
    }
    
    fun calculateFare(pickupLocation: Location, dropLocation: Location, carTypeId: String) {
        viewModelScope.launch {
            try {
<<<<<<< HEAD
                _uiState.value = HomeScreenState.Loading
                
                val currentState = _uiState.value as? HomeScreenState.Success
                val selectedCarType = currentState?.availableCarTypes?.find { it.id == carTypeId }
                
                if (selectedCarType == null) {
                    _uiState.value = HomeScreenState.Error("Selected car type not found")
=======
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)
                
                val selectedCarType = _uiState.value.availableCarTypes.find { it.id == carTypeId }
                
                if (selectedCarType == null) {
                    _uiState.value = _uiState.value.copy(
                        error = "Selected car type not found",
                        isLoading = false
                    )
>>>>>>> 81ec31f166cdb0573d5c5135fcdecb0f6ba49d83
                    return@launch
                }

                val fareDetails = FareDetails(
                    baseFare = selectedCarType.basePrice,
                    distanceFare = 0.0,
                    totalFare = selectedCarType.basePrice,
                    distance = 0.0,
                    estimatedTime = "30 mins",
                    currency = "INR"
                )
                
<<<<<<< HEAD
                _uiState.update { 
                    (it as? HomeScreenState.Success)?.copy(fareDetails = fareDetails) 
                        ?: HomeScreenState.Success(fareDetails = fareDetails)
                }
            } catch (e: Exception) {
                _uiState.value = HomeScreenState.Error(
                    "An error occurred while calculating fare: ${e.message}"
=======
                _uiState.value = _uiState.value.copy(
                    fareDetails = fareDetails,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "An error occurred while calculating fare: ${e.message}",
                    isLoading = false
>>>>>>> 81ec31f166cdb0573d5c5135fcdecb0f6ba49d83
                )
            }
        }
    }
    
    @Inject
    lateinit var monitoringService: MonitoringService

    fun createBooking(bookingRequest: BookingRequest) {
        viewModelScope.launch {
            try {
<<<<<<< HEAD
                _uiState.value = HomeScreenState.Loading
                
                // Implement booking creation logic with the provided request
                _uiState.update {
                    (it as? HomeScreenState.Success)?.copy() 
                        ?: HomeScreenState.Success()
                }
                
                monitoringService.trackRideBooking(bookingRequest)
            } catch (e: Exception) {
                _uiState.value = HomeScreenState.Error(
                    "An error occurred while creating booking: ${e.message}"
                )
                monitoringService.logError(e, "Failed to create booking")
=======
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
>>>>>>> 81ec31f166cdb0573d5c5135fcdecb0f6ba49d83
            }
        }
    }

<<<<<<< HEAD
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
=======
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
>>>>>>> 81ec31f166cdb0573d5c5135fcdecb0f6ba49d83
        }
    }

    fun selectCarType(carType: CarType) {
<<<<<<< HEAD
        _uiState.update {
            (it as? HomeScreenState.Success)?.copy(selectedCarType = carType) 
                ?: HomeScreenState.Success(selectedCarType = carType)
        }
=======
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
>>>>>>> 81ec31f166cdb0573d5c5135fcdecb0f6ba49d83
    }
}
