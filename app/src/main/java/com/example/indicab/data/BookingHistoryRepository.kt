package com.example.indicab.data

import com.example.indicab.data.cache.Cache
import com.example.indicab.data.dao.BookingHistoryDao
import com.example.indicab.models.BookingHistory
import com.example.indicab.models.BookingStatus
import com.example.indicab.utils.NetworkMonitor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class BookingHistoryRepository @Inject constructor(
    private val bookingHistoryDao: BookingHistoryDao,
    private val cache: Cache<Long, BookingHistory>,
    private val networkMonitor: NetworkMonitor,
    private val operationQueue: OperationQueue
) {

    suspend fun createBooking(booking: BookingHistory): Long {
        val id = bookingHistoryDao.insert(booking)
        cache.put(id, booking)
        
        if (networkMonitor.isOnline.first()) {
            // TODO: Make API call to create booking
        } else {
            operationQueue.addOperation(
                OperationQueue.Operation.CreateBooking(booking.copy(id = id))
            )
        }
        
        return id
    }

    suspend fun updateBooking(booking: BookingHistory) {
        bookingHistoryDao.update(booking)
        cache.put(booking.id, booking)
        
        if (networkMonitor.isOnline.first()) {
            // TODO: Make API call to update booking
        } else {
            operationQueue.addOperation(
                OperationQueue.Operation.UpdateBooking(booking)
            )
        }
    }

    fun getBookingsForUser(userId: String): Flow<List<BookingHistory>> {
        return bookingHistoryDao.getBookingsForUser(userId).map { bookings ->
            bookings.forEach { cache.put(it.id, it) }
            bookings
        }
    }

    suspend fun getBookingById(bookingId: Long): BookingHistory? {
        return cache.get(bookingId) ?: bookingHistoryDao.getBookingById(bookingId)?.also {
            cache.put(bookingId, it)
        }
    }

    suspend fun updateBookingStatus(bookingId: Long, status: BookingStatus) {
        bookingHistoryDao.updateBookingStatus(bookingId, status)
        cache.get(bookingId)?.let { booking ->
            cache.put(bookingId, booking.copy(status = status))
        }
        
        if (networkMonitor.isOnline.first()) {
            // TODO: Make API call to update status
        } else {
            operationQueue.addOperation(
                OperationQueue.Operation.UpdateBookingStatus(bookingId, status)
            )
        }
    }
    
    suspend fun syncOperations() {
        if (networkMonitor.isOnline.first()) {
            operationQueue.operations.value.forEach { operation ->
                when (operation) {
                    is OperationQueue.Operation.CreateBooking -> {
                        // TODO: Sync create booking
                    }
                    is OperationQueue.Operation.UpdateBooking -> {
                        // TODO: Sync update booking
                    }
                    is OperationQueue.Operation.UpdateBookingStatus -> {
                        // TODO: Sync status update
                    }
                }
            }
            operationQueue.clearOperations()
        }
    }
}
