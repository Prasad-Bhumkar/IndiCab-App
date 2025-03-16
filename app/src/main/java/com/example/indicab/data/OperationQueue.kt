 package com.example.indicab.data
 
 import com.example.indicab.models.BookingHistory
 import kotlinx.coroutines.flow.MutableStateFlow
 import kotlinx.coroutines.flow.StateFlow
 import javax.inject.Inject
 import javax.inject.Singleton
 
 @Singleton
 class OperationQueue @Inject constructor() {
     private val _operations = MutableStateFlow<List<Operation>>(emptyList())
     val operations: StateFlow<List<Operation>> = _operations
 
     sealed interface Operation {
         data class CreateBooking(val booking: BookingHistory) : Operation
         data class UpdateBooking(val booking: BookingHistory) : Operation
         data class UpdateBookingStatus(val bookingId: Long, val status: BookingStatus) : Operation
     }
 
     fun addOperation(operation: Operation) {
         _operations.value = _operations.value + operation
     }
 
     fun clearOperations() {
         _operations.value = emptyList()
     }
 }
