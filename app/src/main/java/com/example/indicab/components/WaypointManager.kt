 package com.example.indicab.components
 
 import androidx.compose.runtime.getValue
 import androidx.compose.runtime.mutableStateListOf
 import androidx.compose.runtime.mutableStateOf
 import androidx.compose.runtime.setValue
 import com.example.indicab.models.Location
 
 class WaypointManager {
     private val _waypoints = mutableStateListOf<Location>()
     val waypoints: List<Location> get() = _waypoints
 
     var pickupLocation by mutableStateOf<Location?>(null)
     var dropLocation by mutableStateOf<Location?>(null)
 
     fun addWaypoint(location: Location) {
         _waypoints.add(location)
     }
 
     fun removeWaypoint(location: Location) {
         _waypoints.remove(location)
     }
 
     fun clearWaypoints() {
         _waypoints.clear()
     }
 
     fun updateWaypoint(index: Int, location: Location) {
         if (index in _waypoints.indices) {
             _waypoints[index] = location
         }
     }
 
     fun moveWaypoint(fromIndex: Int, toIndex: Int) {
         if (fromIndex in _waypoints.indices && toIndex in _waypoints.indices) {
             val location = _waypoints.removeAt(fromIndex)
             _waypoints.add(toIndex, location)
         }
     }
 
     fun setPickupLocation(location: Location) {
         pickupLocation = location
     }
 
     fun setDropLocation(location: Location) {
         dropLocation = location
     }
 
     fun getRoute(): List<Location> {
         val route = mutableListOf<Location>()
         pickupLocation?.let { route.add(it) }
         route.addAll(_waypoints)
         dropLocation?.let { route.add(it) }
         return route
     }
 }
