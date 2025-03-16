 package com.example.indicab.models
 
 import androidx.room.Entity
 import androidx.room.PrimaryKey
 import java.util.Date
 
 @Entity(tableName = "favorite_locations")
 data class FavoriteLocation(
     @PrimaryKey(autoGenerate = true) val id: Long = 0,
     val userId: String,
     val name: String,
     val address: String,
     val latitude: Double,
     val longitude: Double,
     val createdAt: Date = Date(),
     val updatedAt: Date = Date(),
     val isHome: Boolean = false,
     val isWork: Boolean = false
 )
