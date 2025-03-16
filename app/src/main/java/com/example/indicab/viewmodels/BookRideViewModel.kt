 package com.example.indicab.viewmodels
 
 import androidx.lifecycle.ViewModel
 import androidx.lifecycle.viewModelScope
 import com.example.indicab.models.BookingRequest
 import com.example.indicab.models.CarType
 import com.example.indicab.models.Location
 import com.example.indicab.api.BookingService
 import kotlinx.coroutines.flow.MutableStateFlow
 import kotlinx.coroutines.flow.StateFlow
 import kotlinx.coroutines.launch
 import java.time.LocalDateTime
 
 class BookRideViewModel(
     private val bookingService: BookingService
 ) : ViewModel() {
 
     private val _uiState = MutableStateFlow<BookRideUiState>(BookRideUiState.Initial)
     val uiState: StateFlow<BookRideUiState> = _uiState
 
     private val _availableCars = MutableStateFlow<List<CarType>>(emptyList())
     val availableCars: StateFlow<List<CarType>> = _availableCars
 
     private var pickupLocation: Location? = null
     private var dropLocation: Location? = null
     private var selectedDateTime: LocalDateTime? = null
     private var selectedCarType: CarType? = null
 
     fun setPickupLocation(location: Location) {
         pickupLocation = location
         updateUiState()
     }
 
     fun setDropLocation(location: Location) {
         dropLocation = location
         updateUiState()
     }
 
     fun setDateTime(dateTime: LocalDateTime) {
         selectedDateTime = dateTime
         updateUiState()
     }
 
     fun setCarType(carType: CarType) {
         selectedCarType = carType
         updateUiState()
     }
 
     fun fetchAvailableCars() {
         viewModelScope.launch {
             try {
                 val cars = bookingService.getAvailableCars(
                     pickupLocation!!,
                     dropLocation!!,
                     selectedDateTime!!
                 )
                 _availableCars.value = cars
             } catch (e: Exception) {
                 _uiState.value = BookRideUiState.Error(e.message ?: "Failed to fetch available cars")
             }
         }
     }
 
     fun bookRide() {
         if (!canBookRide()) return
 
         viewModelScope.launch {
             _uiState.value = BookRideUiState.Loading
             try {
                 val bookingRequest = BookingRequest(
                     pickupLocation = pickupLocation!!,
                     dropLocation = dropLocation!!,
                     dateTime = selectedDateTime!!,
                     carType = selectedCarType!!
                 )
                 val booking = bookingService.createBooking(bookingRequest)
                 _uiState.value = BookRideUiState.Success(booking)
             } catch (e: Exception) {
                 _uiState.value = BookRideUiState.Error(e.message ?: "Failed to book ride")
             }
         }
     }
 
     private fun canBookRide(): Boolean {
         return pickupLocation != null && 
                dropLocation != null && 
                selectedDateTime != null && 
                selectedCarType != null
     }
 
     private fun updateUiState() {
         _uiState.value = if (canBookRide()) {
             BookRideUiState.ReadyToBook
         } else {
             BookRideUiState.Initial
         }
     }
 }
 
 sealed class BookRideUiState {
     object Initial : BookRideUiState()
     object ReadyToBook : BookRideUiState()
     object Loading : BookRideUiState()
     data class Success(val booking: Any) : BookRideUiState()
     data class Error(val message: String) : BookRideUiState()
 }
