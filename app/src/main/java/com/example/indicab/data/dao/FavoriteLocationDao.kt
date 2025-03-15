package com.example.indicab.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.indicab.models.FavoriteLocation
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteLocationDao {
    @Insert
    suspend fun insert(favoriteLocation: FavoriteLocation): Long

    @Update
    suspend fun update(favoriteLocation: FavoriteLocation)

    @Query("DELETE FROM favorite_locations WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("SELECT * FROM favorite_locations WHERE userId = :userId ORDER BY name ASC")
    fun getFavoriteLocations(userId: String): Flow<List<FavoriteLocation>>

    @Query("SELECT * FROM favorite_locations WHERE userId = :userId AND isHome = 1 LIMIT 1")
    suspend fun getHomeLocation(userId: String): FavoriteLocation?

    @Query("SELECT * FROM favorite_locations WHERE userId = :userId AND isWork = 1 LIMIT 1")
    suspend fun getWorkLocation(userId: String): FavoriteLocation?

    @Query("SELECT * FROM favorite_locations WHERE userId = :userId AND (isHome = 1 OR isWork = 1)")
    fun getPrimaryLocations(userId: String): Flow<List<FavoriteLocation>>
}
