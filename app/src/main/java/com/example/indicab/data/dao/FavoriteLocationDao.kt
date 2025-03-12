package com.example.indicab.data.dao

import androidx.room.*
import com.example.indicab.models.FavoriteLocation
import com.example.indicab.models.FavoriteLocationType
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

@Dao
interface FavoriteLocationDao {
    @Query("SELECT * FROM favorite_locations WHERE userId = :userId ORDER BY usageCount DESC, lastUsed DESC")
    fun getAllFavoriteLocations(userId: String): Flow<List<FavoriteLocation>>

    @Query("SELECT * FROM favorite_locations WHERE userId = :userId AND type = :type ORDER BY usageCount DESC, lastUsed DESC")
    fun getFavoriteLocationsByType(userId: String, type: FavoriteLocationType): Flow<List<FavoriteLocation>>

    @Query("""
        SELECT * FROM favorite_locations 
        WHERE userId = :userId 
        AND (label LIKE '%' || :query || '%' OR tags LIKE '%' || :query || '%')
        ORDER BY usageCount DESC, lastUsed DESC
    """)
    fun searchFavoriteLocations(userId: String, query: String): Flow<List<FavoriteLocation>>

    @Query("SELECT * FROM favorite_locations WHERE id = :id AND userId = :userId")
    suspend fun getFavoriteLocationById(id: String, userId: String): FavoriteLocation?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteLocation(location: FavoriteLocation)

    @Update
    suspend fun updateFavoriteLocation(location: FavoriteLocation)

    @Delete
    suspend fun deleteFavoriteLocation(location: FavoriteLocation)

    @Query("DELETE FROM favorite_locations WHERE userId = :userId")
    suspend fun deleteAllFavoriteLocations(userId: String)

    @Query("""
        UPDATE favorite_locations 
        SET usageCount = usageCount + 1, 
            lastUsed = :lastUsed,
            updatedAt = :lastUsed
        WHERE id = :id AND userId = :userId
    """)
    suspend fun incrementUsageCount(id: String, userId: String, lastUsed: LocalDateTime = LocalDateTime.now())

    @Transaction
    @Query("""
        SELECT * FROM favorite_locations 
        WHERE userId = :userId 
        ORDER BY 
            CASE type 
                WHEN 'HOME' THEN 1 
                WHEN 'WORK' THEN 2 
                ELSE 3 
            END,
            usageCount DESC, 
            lastUsed DESC 
        LIMIT :limit
    """)
    fun getRecentFavoriteLocations(userId: String, limit: Int = 5): Flow<List<FavoriteLocation>>

    @Query("""
        SELECT * FROM favorite_locations 
        WHERE userId = :userId 
        AND type IN ('HOME', 'WORK') 
        ORDER BY type
    """)
    fun getHomeAndWorkLocations(userId: String): Flow<List<FavoriteLocation>>

    @Transaction
    suspend fun addOrUpdateFavoriteLocation(location: FavoriteLocation) {
        val existing = getFavoriteLocationById(location.id, location.userId)
        if (existing != null) {
            updateFavoriteLocation(location.copy(
                usageCount = existing.usageCount,
                createdAt = existing.createdAt,
                updatedAt = LocalDateTime.now()
            ))
        } else {
            insertFavoriteLocation(location)
        }
    }

    @Query("""
        SELECT DISTINCT tag 
        FROM (
            SELECT TRIM(value) as tag
            FROM favorite_locations
            CROSS JOIN json_each('["' || REPLACE(tags, ',', '","') || '"]')
            WHERE userId = :userId
        )
        WHERE tag != ''
        ORDER BY tag
    """)
    fun getAllTags(userId: String): Flow<List<String>>
}
