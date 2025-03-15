package com.example.indicab.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.indicab.models.RideHistory
import kotlinx.coroutines.flow.Flow

@Dao
interface RideHistoryDao {

    @Insert
    suspend fun insert(rideHistory: RideHistory): Long

    @Query("SELECT * FROM ride_history WHERE userId = :userId ORDER BY timestamp DESC")
    fun getRideHistoryForUser(userId: String): Flow<List<RideHistory>>

    @Query("SELECT * FROM ride_history WHERE id = :rideId")
    suspend fun getRideById(rideId: Long): RideHistory?

    @Update
    suspend fun update(rideHistory: RideHistory)

    @Query("DELETE FROM ride_history WHERE id = :rideId")
    suspend fun delete(rideId: Long)
}
