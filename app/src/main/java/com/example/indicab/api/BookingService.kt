package com.example.indicab.api

import com.example.indicab.models.BookingRequest
import com.example.indicab.models.CarType
import com.example.indicab.models.FareDetails
import retrofit2.Response
import retrofit2.http.*
import com.example.indicab.models.FareDetails
import com.example.indicab.models.BookingRequest
import retrofit2.http.Body
import retrofit2.http.*

interface BookingService {
    @GET("car-types")
    suspend fun getCarTypes(): Response<List<CarType>>

    @POST("calculate-fare")
    companion object {
        private val retrofit = retrofit2.Retrofit.Builder()
            .baseUrl("https://api.example.com/") // Replace with actual base URL
            .build()

        private val service = retrofit.create(BookingService::class.java)
    }

    suspend fun calculateFare(@Body request: BookingRequest): Response<FareDetails> {
        return try {
            val response = service.calculateFare(request)
            if (response.isSuccessful) {
                response
            } else {
                Response.error(response.code(), response.errorBody()!!)
            }
        } catch (e: Exception) {
            Response.error(500, null) // Handle the error case
        }
    }

    @POST("bookings")
    suspend fun createBooking(@Body request: BookingRequest): Response<BookingRequest>

    @GET("bookings/{id}")
    suspend fun getBooking(@Path("id") id: String): Response<BookingRequest>
}
