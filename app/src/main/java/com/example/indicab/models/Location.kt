package com.example.indicab.models

data class Location(
    val id: String = "",
    val name: String = "",
    val address: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val placeId: String = ""
) 