package com.example.indicab

import kotlin.math.*

data class Location(
    val id: String = "",
    val name: String = "",
    val address: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val timestamp: Long = System.currentTimeMillis()
) {
    companion object {
        private const val EARTH_RADIUS = 6371 // Radius of the earth in km
    }

    fun isValid(): Boolean {
        return latitude in -90.0..90.0 && longitude in -180.0..180.0
    }

    fun distanceTo(other: Location): Double {
        if (!isValid() || !other.isValid()) return 0.0

        val latDistance = Math.toRadians(other.latitude - latitude)
        val lonDistance = Math.toRadians(other.longitude - longitude)
        val a = sin(latDistance / 2) * sin(latDistance / 2) +
                cos(Math.toRadians(latitude)) * cos(Math.toRadians(other.latitude)) *
                sin(lonDistance / 2) * sin(lonDistance / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return EARTH_RADIUS * c
    }

    override fun toString(): String {
        return "Location(name='$name', address='$address', lat=$latitude, lon=$longitude)"
    }
}
