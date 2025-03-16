 package com.example.indicab.models
 
 data class BookingRequest(
     val id: String = "",
     val userId: String = "",
     val carTypeId: String = "",
     val route: RouteWithWaypoints? = null,
     // Keeping these for backward compatibility
     val pickupLocation: Location = Location(),
     val dropLocation: Location = Location(),
     val pickupTime: Long = 0L,
     val tripType: String = "ONE_WAY",
     val status: String = "PENDING",
     val fareDetails: FareDetails? = null,
     val createdAt: Long = System.currentTimeMillis(),
     // Additional fields used in HomeScreenActivity
     val date: String = "",
     val time: String = "",
     val carType: String = "",
     val passengers: Int = 0
 ) {
     // Helper properties to maintain compatibility
     val hasMultipleStops: Boolean
         get() = route?.stops?.isNotEmpty() ?: false
 
     // Convert legacy locations to RouteWithWaypoints
     fun toRouteWithWaypoints(): RouteWithWaypoints {
         return route ?: RouteWithWaypoints(
             waypoints = listOfNotNull(
                 Waypoint(
                     location = pickupLocation,
                     order = 0,
                     type = WaypointType.PICKUP
                 ),
                 Waypoint(
                     location = dropLocation,
                     order = Int.MAX_VALUE,
                     type = WaypointType.DROPOFF
                 )
             )
         )
     }
 
     companion object {
         fun fromRoute(
             route: RouteWithWaypoints,
             userId: String = "",
             carTypeId: String = "",
             tripType: String = "ONE_WAY",
             status: String = "PENDING",
             fareDetails: FareDetails? = null,
             date: String = "",
             time: String = "",
             carType: String = "",
             passengers: Int = 0
         ): BookingRequest {
             return BookingRequest(
                 id = "BK" + System.currentTimeMillis(),
                 userId = userId,
                 carTypeId = carTypeId,
                 route = route,
                 // Set legacy fields from route
                 pickupLocation = route.pickup?.location ?: Location(),
                 dropLocation = route.dropoff?.location ?: Location(),
                 pickupTime = System.currentTimeMillis(),
                 tripType = tripType,
                 status = status,
                 fareDetails = fareDetails,
                 date = date,
                 time = time,
                 carType = carType,
                 passengers = passengers
             )
         }
     }
 }
