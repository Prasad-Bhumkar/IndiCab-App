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

class FavoriteLocationRepository(private val favoriteLocationDao: FavoriteLocationDao) {
    fun getFavoriteLocations(userId: String) = 
        favoriteLocationDao.getFavoriteLocations(userId)

    suspend fun getHomeLocation(userId: String) = 
        favoriteLocationDao.getHomeLocation(userId)

    suspend fun getWorkLocation(userId: String) = 
        favoriteLocationDao.getWorkLocation(userId)

    fun getPrimaryLocations(userId: String) = 
        favoriteLocationDao.getPrimaryLocations(userId)

    suspend fun insert(favoriteLocation: FavoriteLocation) = 
        favoriteLocationDao.insert(favoriteLocation)

    suspend fun update(favoriteLocation: FavoriteLocation) = 
        favoriteLocationDao.update(favoriteLocation)

    suspend fun delete(id: Long) = 
        favoriteLocationDao.delete(id)
}
