package com.example.indicab.data.repositories

import com.example.indicab.api.BookingService
import com.example.indicab.models.BookingRequest
import com.example.indicab.models.CarType
import com.example.indicab.models.FareDetails
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response

class BookingRepository(
    private val bookingService: BookingService
) {
    suspend fun getCarTypes(): Flow<Response<List<CarType>>> = flow {
        emit(bookingService.getCarTypes())
    }

    suspend fun calculateFare(request: BookingRequest): Flow<Response<FareDetails>> = flow {
        emit(bookingService.calculateFare(request))
    }

    suspend fun createBooking(request: BookingRequest): Flow<Response<BookingRequest>> = flow {
        emit(bookingService.createBooking(request))
    }

    suspend fun getBooking(id: String): Flow<Response<BookingRequest>> = flow {
        emit(bookingService.getBooking(id))
    }

    // Helper function to handle API responses
    private fun <T> handleApiResponse(response: Response<T>): Result<T> {
        return if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                Result.success(body)
            } else {
                Result.failure(Exception("Response body is null"))
            }
        } else {
            Result.failure(Exception("API call failed with code: ${response.code()}"))
        }
    }

    // Booking state management
    sealed class BookingState {
        object Loading : BookingState()
        data class Success<T>(val data: T) : BookingState()
        data class Error(val message: String) : BookingState()
    }

    // API response wrapper
    sealed class ApiResult<out T> {
        data class Success<out T>(val data: T) : ApiResult<T>()
        data class Error(val exception: Exception) : ApiResult<Nothing>()
        object Loading : ApiResult<Nothing>()
    }

    companion object {
        private const val TAG = "BookingRepository"
    }
}
