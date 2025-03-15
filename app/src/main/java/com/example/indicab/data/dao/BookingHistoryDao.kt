package com.example.indicab.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.indicab.models.BookingHistory
import kotlinx.coroutines.flow.Flow

@Dao
interface BookingHistoryDao {

    @Insert
    suspend fun insert(booking: BookingHistory): Long

    @Update
    suspend fun update(booking: BookingHistory)

    @Query("SELECT * FROM booking_history WHERE userId = :userId ORDER BY timestamp DESC")
    fun getBookingsForUser(userId: String): Flow<List<BookingHistory>>

    @Query("SELECT * FROM booking_history WHERE id = :bookingId")
    suspend fun getBookingById(bookingId: Long): BookingHistory?

    @Query("UPDATE booking_history SET status = :status WHERE id = :bookingId")
    suspend fun updateBookingStatus(bookingId: Long, status: BookingStatus)
}
