 package com.example.indicab.api
 
 import com.example.indicab.models.BookingRequest
 import com.example.indicab.models.CarType
 import com.example.indicab.models.FareDetails
 import retrofit2.Response
 import retrofit2.http.*
 
 interface BookingService {
     @GET("car-types")
     suspend fun getCarTypes(): Response<List<CarType>>
 
     @POST("calculate-fare")
     suspend fun calculateFare(@Body request: BookingRequest): Response<FareDetails>
 
     @POST("bookings")
     suspend fun createBooking(@Body request: BookingRequest): Response<BookingRequest>
 
     @GET("bookings/{id}")
     suspend fun getBooking(@Path("id") id: String): Response<BookingRequest>
 } 