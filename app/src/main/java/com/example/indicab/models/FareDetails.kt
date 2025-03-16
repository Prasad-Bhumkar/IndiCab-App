 package com.example.indicab.models
 
 data class FareDetails(
     val baseFare: Double,
     val distanceFare: Double,
     val totalFare: Double,
     val distance: Double,
     val estimatedTime: String,
     val currency: String
 ) 