package com.example.indicab.utils

import com.example.indicab.models.Location
import kotlin.math.*

object LocationUtils {
    private const val EARTH_RADIUS_KM = 6371.0

    /**
     * Calculate distance between two locations using Haversine formula
     */
    fun calculateDistance(location1: Location, location2: Location): Double {
        val lat1 = Math.toRadians(location1.latitude)
        val lon1 = Math.toRadians(location1.longitude)
        val lat2 = Math.toRadians(location2.latitude)
        val lon2 = Math.toRadians(location2.longitude)

        val dLat = lat2 - lat1
        val dLon = lon2 - lon1

        val a = sin(dLat / 2).pow(2.0) +
                cos(lat1) * cos(lat2) *
                sin(dLon / 2).pow(2.0)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return EARTH_RADIUS_KM * c
    }

    /**
     * Calculate total distance for a route with multiple waypoints
     */
    fun calculateRouteDistance(route: List<Location>): Double {
        if (route.size < 2) return 0.0
        
        var totalDistance = 0.0
        for (i in 0 until route.size - 1) {
            totalDistance += calculateDistance(route[i], route[i + 1])
        }
        return totalDistance
    }

    /**
     * Calculate estimated fare based on distance and number of waypoints
     */
    fun calculateFare(baseFare: Double, distance: Double, waypointsCount: Int): Double {
        // Base fare per km
        val perKmRate = baseFare / 5.0 // Assuming base fare is for 5 km
        
        // Additional charge per waypoint
        val waypointCharge = 10.0 * waypointsCount
        
        // Minimum fare
        val minimumFare = baseFare
        
        // Calculate total fare
        val calculatedFare = (distance * perKmRate) + waypointCharge
        
        // Return the higher of calculated fare or minimum fare
        return max(calculatedFare, minimumFare)
    }
}
