package com.example.indicab.models

import java.time.LocalDateTime

data class Waypoint(
    val id: String = "",
    val location: Location,
    val order: Int,
    val stopDuration: Int = 0, // Duration in minutes
    val scheduledArrival: LocalDateTime? = null,
    val notes: String = "",
    val type: WaypointType = WaypointType.STOP
)

enum class WaypointType {
    PICKUP,    // Starting point
    STOP,      // Intermediate stop
    DROPOFF    // Final destination
}

data class RouteWithWaypoints(
    val id: String = "",
    val waypoints: List<Waypoint>,
    val totalDistance: Double = 0.0,
    val estimatedDuration: Int = 0, // Duration in minutes
    val createdAt: LocalDateTime = LocalDateTime.now()
) {
    val pickup: Waypoint?
        get() = waypoints.firstOrNull { it.type == WaypointType.PICKUP }
    
    val dropoff: Waypoint?
        get() = waypoints.firstOrNull { it.type == WaypointType.DROPOFF }
    
    val stops: List<Waypoint>
        get() = waypoints.filter { it.type == WaypointType.STOP }
}
