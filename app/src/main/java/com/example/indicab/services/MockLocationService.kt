package com.example.indicab.services

import android.location.Location
import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class MockLocationService @Inject constructor() : LocationService {
    private var currentLocation = Location("mock").apply {
        // Default location (Mumbai)
        latitude = 19.0760
        longitude = 72.8777
        accuracy = 10f
        speed = 0f
        bearing = 0f
        time = System.currentTimeMillis()
    }

    private val landmarks = listOf(
        Landmark(19.0760, 72.8777, "Chhatrapati Shivaji Terminus"),
        Landmark(18.9220, 72.8347, "Gateway of India"),
        Landmark(19.0178, 72.8478, "Haji Ali Dargah"),
        Landmark(19.0454, 72.8193, "Bandra-Worli Sea Link"),
        Landmark(19.0368, 72.8425, "Siddhivinayak Temple"),
        Landmark(19.0139, 72.8254, "Worli Sea Face"),
        Landmark(19.0749, 72.8753, "Dharavi"),
        Landmark(19.2147, 72.9753, "Powai Lake"),
        Landmark(19.0948, 72.8629, "Dadar Railway Station"),
        Landmark(19.0596, 72.8295, "Mahim Bay")
    )

    private var simulatedSpeed = 0.0
    private var simulatedBearing = 0.0
    private var isMoving = false

    override suspend fun getCurrentLocation(): Location {
        // Simulate network delay
        delay(Random.nextLong(100, 500))

        if (isMoving) {
            // Update location based on speed and bearing
            val distanceInMeters = (simulatedSpeed * 1000 / 3600) // Convert km/h to m/s
            val newLocation = calculateNewLocation(
                currentLocation.latitude,
                currentLocation.longitude,
                simulatedBearing,
                distanceInMeters
            )
            currentLocation.apply {
                latitude = newLocation.first
                longitude = newLocation.second
                speed = simulatedSpeed.toFloat()
                bearing = simulatedBearing.toFloat()
                time = System.currentTimeMillis()
            }
        }

        return currentLocation
    }

    override suspend fun getCurrentSpeed(): Double {
        delay(Random.nextLong(50, 200))
        return simulatedSpeed
    }

    override suspend fun getNearestLandmark(location: Location): String {
        delay(Random.nextLong(100, 300))
        
        return landmarks.minByOrNull { landmark ->
            val landmarkLocation = Location("landmark").apply {
                latitude = landmark.latitude
                longitude = landmark.longitude
            }
            location.distanceTo(landmarkLocation)
        }?.name ?: "Unknown Location"
    }

    // Test helper methods
    fun setLocation(latitude: Double, longitude: Double) {
        currentLocation.apply {
            this.latitude = latitude
            this.longitude = longitude
            time = System.currentTimeMillis()
        }
    }

    fun startMoving(speed: Double, bearing: Double) {
        simulatedSpeed = speed
        simulatedBearing = bearing
        isMoving = true
    }

    fun stopMoving() {
        simulatedSpeed = 0.0
        isMoving = false
    }

    fun simulateAccuracy(accuracy: Float) {
        currentLocation.accuracy = accuracy
    }

    fun simulateLocationError() {
        // Add random offset to simulate GPS inaccuracy
        val latOffset = Random.nextDouble(-0.0001, 0.0001)
        val lonOffset = Random.nextDouble(-0.0001, 0.0001)
        currentLocation.apply {
            latitude += latOffset
            longitude += lonOffset
            accuracy = Random.nextFloat() * 20 + 10 // 10-30m accuracy
        }
    }

    private fun calculateNewLocation(
        lat: Double,
        lon: Double,
        bearing: Double,
        distance: Double
    ): Pair<Double, Double> {
        val R = 6371000.0 // Earth's radius in meters
        val bearingRad = Math.toRadians(bearing)
        val latRad = Math.toRadians(lat)
        val lonRad = Math.toRadians(lon)
        
        val distRatio = distance / R
        val newLatRad = Math.asin(
            Math.sin(latRad) * Math.cos(distRatio) +
            Math.cos(latRad) * Math.sin(distRatio) * Math.cos(bearingRad)
        )
        
        val newLonRad = lonRad + Math.atan2(
            Math.sin(bearingRad) * Math.sin(distRatio) * Math.cos(latRad),
            Math.cos(distRatio) - Math.sin(latRad) * Math.sin(newLatRad)
        )

        return Pair(
            Math.toDegrees(newLatRad),
            Math.toDegrees(newLonRad)
        )
    }

    // Predefined routes for testing
    fun simulateRoute(route: Route) {
        when (route) {
            Route.MUMBAI_AIRPORT_TO_CITY -> {
                setLocation(19.0896, 72.8656) // Mumbai Airport
                startMoving(30.0, 180.0) // Moving south at 30 km/h
            }
            Route.BANDRA_TO_COLABA -> {
                setLocation(19.0596, 72.8295) // Bandra
                startMoving(25.0, 160.0) // Moving southeast at 25 km/h
            }
            Route.THANE_TO_MUMBAI -> {
                setLocation(19.2183, 72.9781) // Thane
                startMoving(35.0, 225.0) // Moving southwest at 35 km/h
            }
        }
    }

    enum class Route {
        MUMBAI_AIRPORT_TO_CITY,
        BANDRA_TO_COLABA,
        THANE_TO_MUMBAI
    }

    private data class Landmark(
        val latitude: Double,
        val longitude: Double,
        val name: String
    )
}
