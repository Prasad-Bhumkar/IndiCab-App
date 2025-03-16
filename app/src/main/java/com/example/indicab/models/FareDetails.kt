<<<<<<< HEAD
 package com.example.indicab.models
 
 data class FareDetails(
     val baseFare: Double,
     val distanceFare: Double,
     val totalFare: Double,
     val distance: Double,
     val estimatedTime: String,
     val currency: String
 ) 
=======
package com.example.indicab.models

data class FareDetails(
    val baseFare: Double,
    val distanceFare: Double,
    val totalFare: Double,
    val distance: Double,
    val estimatedTime: String,
    val currency: String
) {
    fun calculateTotalFare(): Double {
        return baseFare + distanceFare
    }
}
>>>>>>> 81ec31f166cdb0573d5c5135fcdecb0f6ba49d83
