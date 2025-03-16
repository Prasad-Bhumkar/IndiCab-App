package com.example.indicab.data.repositories

import com.example.indicab.data.dao.FavoriteLocationDao
import com.example.indicab.models.FavoriteLocation
import com.example.indicab.models.FavoriteLocationType
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

class LocationRepository(
    private val favoriteLocationDao: FavoriteLocationDao
) {
    // Favorite locations operations
    fun getAllFavoriteLocations(userId: String): Flow<List<FavoriteLocation>> =
        favoriteLocationDao.getAllFavoriteLocations(userId)

    fun getFavoriteLocationsByType(userId: String, type: FavoriteLocationType): Flow<List<FavoriteLocation>> =
        favoriteLocationDao.getFavoriteLocationsByType(userId, type)

    fun searchFavoriteLocations(userId: String, query: String): Flow<List<FavoriteLocation>> =
        favoriteLocationDao.searchFavoriteLocations(userId, query)

    suspend fun getFavoriteLocationById(id: String, userId: String): FavoriteLocation? =
        favoriteLocationDao.getFavoriteLocationById(id, userId)

    suspend fun addFavoriteLocation(location: FavoriteLocation) =
        favoriteLocationDao.addOrUpdateFavoriteLocation(location)

    suspend fun updateFavoriteLocation(location: FavoriteLocation) =
        favoriteLocationDao.updateFavoriteLocation(location)

    suspend fun deleteFavoriteLocation(location: FavoriteLocation) =
        favoriteLocationDao.deleteFavoriteLocation(location)

    suspend fun deleteAllFavoriteLocations(userId: String) =
        favoriteLocationDao.deleteAllFavoriteLocations(userId)

    suspend fun incrementLocationUsage(id: String, userId: String) =
        favoriteLocationDao.incrementUsageCount(id, userId, LocalDateTime.now())

    fun getRecentFavoriteLocations(userId: String, limit: Int = 5): Flow<List<FavoriteLocation>> =
        favoriteLocationDao.getRecentFavoriteLocations(userId, limit)

    fun getHomeAndWorkLocations(userId: String): Flow<List<FavoriteLocation>> =
        favoriteLocationDao.getHomeAndWorkLocations(userId)

    fun getAllTags(userId: String): Flow<List<String>> =
        favoriteLocationDao.getAllTags(userId)

    // Location state management
    sealed class LocationState {
        object Loading : LocationState()
        data class Success<T>(val data: T) : LocationState()
        data class Error(val message: String) : LocationState()
    }

    companion object {
        private const val TAG = "LocationRepository"
    }
}
