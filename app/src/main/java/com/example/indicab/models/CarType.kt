 package com.example.indicab.models
 
 data class CarType(
     val id: String,
     val name: String,
     val description: String,
     val basePrice: Double,
     val pricePerKm: Double,
     val capacity: Int,
     val imageUrl: String
 ) 