 package com.example.indicab.viewmodels
 
 import androidx.lifecycle.ViewModel
 import androidx.lifecycle.viewModelScope
 import com.example.indicab.models.*
 import kotlinx.coroutines.flow.MutableStateFlow
 import kotlinx.coroutines.flow.StateFlow
 import kotlinx.coroutines.launch
 import java.time.LocalDateTime
 
 class WaypointViewModel : ViewModel() {
     private val _routeState = MutableStateFlow<RouteState>(RouteState.Initial)
     val routeState: StateFlow<RouteState> = _routeState
 
     private val _waypoints = MutableStateFlow<List<Waypoint>>(emptyList())
     val waypoints: StateFlow<List<Waypoint>> = _waypoints
 
     private val _selectedWaypoint = MutableStateFlow<Waypoint?>(null)
     val selectedWaypoint: StateFlow<Waypoint?> = _selectedWaypoint
 
     fun addWaypoint(location: Location, type: WaypointType = WaypointType.STOP) {
         viewModelScope.launch {
             try {
                 val currentWaypoints = _waypoints.value.toMutableList()
                 val newWaypoint = Waypoint(
                     id = generateWaypointId(),
                     location = location,
                     order = when (type) {
                         WaypointType.PICKUP -> 0
                         WaypointType.DROPOFF -> Int.MAX_VALUE
                         WaypointType.STOP -> currentWaypoints.size + 1
                     },
                     type = type
                 )
 
                 currentWaypoints.add(newWaypoint)
                 reorderWaypoints(currentWaypoints)
                 
                 _waypoints.value = currentWaypoints
                 calculateRoute()
             } catch (e: Exception) {
                 _routeState.value = RouteState.Error("Failed to add waypoint: ${e.message}")
             }
         }
     }
 
     fun removeWaypoint(waypointId: String) {
         viewModelScope.launch {
             try {
                 val currentWaypoints = _waypoints.value.toMutableList()
                 currentWaypoints.removeAll { it.id == waypointId }
                 reorderWaypoints(currentWaypoints)
                 
                 _waypoints.value = currentWaypoints
                 calculateRoute()
             } catch (e: Exception) {
                 _routeState.value = RouteState.Error("Failed to remove waypoint: ${e.message}")
             }
         }
     }
 
     fun updateWaypointOrder(waypointId: String, newOrder: Int) {
         viewModelScope.launch {
             try {
                 val currentWaypoints = _waypoints.value.toMutableList()
                 val waypoint = currentWaypoints.find { it.id == waypointId } ?: return@launch
                 
                 currentWaypoints.remove(waypoint)
                 currentWaypoints.add(newOrder, waypoint)
                 reorderWaypoints(currentWaypoints)
                 
                 _waypoints.value = currentWaypoints
                 calculateRoute()
             } catch (e: Exception) {
                 _routeState.value = RouteState.Error("Failed to update waypoint order: ${e.message}")
             }
         }
     }
 
     fun updateWaypointDetails(
         waypointId: String,
         stopDuration: Int? = null,
         notes: String? = null,
         scheduledArrival: LocalDateTime? = null
     ) {
         viewModelScope.launch {
             try {
                 val currentWaypoints = _waypoints.value.toMutableList()
                 val index = currentWaypoints.indexOfFirst { it.id == waypointId }
                 if (index != -1) {
                     val waypoint = currentWaypoints[index]
                     currentWaypoints[index] = waypoint.copy(
                         stopDuration = stopDuration ?: waypoint.stopDuration,
                         notes = notes ?: waypoint.notes,
                         scheduledArrival = scheduledArrival ?: waypoint.scheduledArrival
                     )
                     _waypoints.value = currentWaypoints
                     calculateRoute()
                 }
             } catch (e: Exception) {
                 _routeState.value = RouteState.Error("Failed to update waypoint details: ${e.message}")
             }
         }
     }
 
     private fun calculateRoute() {
         viewModelScope.launch {
             _routeState.value = RouteState.Loading
             try {
                //  Integrate with actual routing service
                 // For now, just create a route with the current waypoints
                 val route = RouteWithWaypoints(
                     id = generateRouteId(),
                     waypoints = _waypoints.value,
                     totalDistance = calculateTotalDistance(),
                     estimatedDuration = calculateEstimatedDuration()
                 )
                 _routeState.value = RouteState.Success(route)
             } catch (e: Exception) {
                 _routeState.value = RouteState.Error("Failed to calculate route: ${e.message}")
             }
         }
     }
 
     private fun reorderWaypoints(waypoints: MutableList<Waypoint>) {
         // Sort waypoints: PICKUP first, then STOPS in order, DROPOFF last
         waypoints.sortWith(compareBy(
             { it.type == WaypointType.PICKUP },
             { it.type == WaypointType.STOP },
             { it.type == WaypointType.DROPOFF },
             { it.order }
         ))
 
         // Update order numbers
         waypoints.forEachIndexed { index, waypoint ->
             if (waypoint.type == WaypointType.STOP) {
                 waypoints[index] = waypoint.copy(order = index)
             }
         }
     }
 
     private fun calculateTotalDistance(): Double {
        //  Implement actual distance calculation using Maps API
         return 0.0
     }
 
     private fun calculateEstimatedDuration(): Int {
        //  Implement actual duration calculation using Maps API
         return 0
     }
 
     private fun generateWaypointId(): String {
         return "WP" + System.currentTimeMillis()
     }
 
     private fun generateRouteId(): String {
         return "RT" + System.currentTimeMillis()
     }
 }
 
 sealed class RouteState {
     object Initial : RouteState()
     object Loading : RouteState()
     data class Success(val route: RouteWithWaypoints) : RouteState()
     data class Error(val message: String) : RouteState()
 }
