package com.example.indicab.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "favorite_locations")
data class FavoriteLocation(
    @PrimaryKey
    val id: String = "FL" + System.currentTimeMillis(),
    val userId: String,
    val location: Location,
    val label: String,
    val tags: List<String> = emptyList(),
    val type: FavoriteLocationType = FavoriteLocationType.OTHER,
    val usageCount: Int = 0,
    val lastUsed: LocalDateTime? = null,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

enum class FavoriteLocationType {
    HOME,
    WORK,
    FAMILY,
    FRIEND,
    OTHER
}

data class FavoriteLocationGroup(
    val type: FavoriteLocationType,
    val locations: List<FavoriteLocation>
)

// Type converters for Room
class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): LocalDateTime? {
        return value?.let { LocalDateTime.ofEpochSecond(it, 0, ZoneOffset.UTC) }
    }

    @TypeConverter
    fun dateToTimestamp(date: LocalDateTime?): Long? {
        return date?.toEpochSecond(ZoneOffset.UTC)
    }

    @TypeConverter
    fun fromTagList(value: String): List<String> {
        return if (value.isBlank()) emptyList() else value.split(",")
    }

    @TypeConverter
    fun toTagList(tags: List<String>): String {
        return tags.joinToString(",")
    }

    @TypeConverter
    fun fromLocation(location: Location): String {
        return Gson().toJson(location)
    }

    @TypeConverter
    fun toLocation(value: String): Location {
        return Gson().fromJson(value, Location::class.java)
    }
}
