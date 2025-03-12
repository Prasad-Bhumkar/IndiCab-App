package com.example.indicab.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.indicab.data.dao.FavoriteLocationDao
import com.example.indicab.models.Converters
import com.example.indicab.models.FavoriteLocation

@Database(
    entities = [FavoriteLocation::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteLocationDao(): FavoriteLocationDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "indicab_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

// Repository class for favorite locations
class FavoriteLocationRepository(private val favoriteLocationDao: FavoriteLocationDao) {
    fun getAllFavoriteLocations(userId: String) = 
        favoriteLocationDao.getAllFavoriteLocations(userId)

    fun getFavoriteLocationsByType(userId: String, type: FavoriteLocationType) =
        favoriteLocationDao.getFavoriteLocationsByType(userId, type)

    fun searchFavoriteLocations(userId: String, query: String) =
        favoriteLocationDao.searchFavoriteLocations(userId, query)

    fun getRecentFavoriteLocations(userId: String, limit: Int = 5) =
        favoriteLocationDao.getRecentFavoriteLocations(userId, limit)

    fun getHomeAndWorkLocations(userId: String) =
        favoriteLocationDao.getHomeAndWorkLocations(userId)

    fun getAllTags(userId: String) = 
        favoriteLocationDao.getAllTags(userId)

    suspend fun addFavoriteLocation(location: FavoriteLocation) =
        favoriteLocationDao.addOrUpdateFavoriteLocation(location)

    suspend fun updateFavoriteLocation(location: FavoriteLocation) =
        favoriteLocationDao.updateFavoriteLocation(location)

    suspend fun deleteFavoriteLocation(location: FavoriteLocation) =
        favoriteLocationDao.deleteFavoriteLocation(location)

    suspend fun incrementUsageCount(id: String, userId: String) =
        favoriteLocationDao.incrementUsageCount(id, userId)

    suspend fun getFavoriteLocationById(id: String, userId: String) =
        favoriteLocationDao.getFavoriteLocationById(id, userId)
}
