 package com.example.indicab.data
 
 import com.example.indicab.data.dao.RideHistoryDao
 import com.example.indicab.models.RideHistory
 import kotlinx.coroutines.flow.Flow
 import javax.inject.Inject
 
 class RideHistoryRepository @Inject constructor(
     private val rideHistoryDao: RideHistoryDao
 ) {
 
     suspend fun addRideHistory(rideHistory: RideHistory): Long {
         return rideHistoryDao.insert(rideHistory)
     }
 
     fun getRideHistoryForUser(userId: String): Flow<List<RideHistory>> {
         return rideHistoryDao.getRideHistoryForUser(userId)
     }
 
     suspend fun getRideById(rideId: Long): RideHistory? {
         return rideHistoryDao.getRideById(rideId)
     }
 
     @Inject
     lateinit var monitoringService: MonitoringService
 
     suspend fun updateRideHistory(rideHistory: RideHistory) {
         rideHistoryDao.update(rideHistory)
         
         // Track rating if it was updated
         rideHistory.rating?.let { rating ->
             monitoringService.trackRating(rating)
         }
     }
 
     suspend fun deleteRideHistory(rideId: Long) {
         rideHistoryDao.delete(rideId)
     }
 
     suspend fun submitRating(rideId: Long, rating: com.example.indicab.models.Rating) {
         val ride = rideHistoryDao.getRideById(rideId) ?: return
         val updatedRide = ride.copy(rating = rating)
         rideHistoryDao.update(updatedRide)
     }
 
     suspend fun submitReview(rideId: Long, review: String) {
         val ride = rideHistoryDao.getRideById(rideId) ?: return
         val updatedRide = ride.copy(review = review)
         rideHistoryDao.update(updatedRide)
     }
 
     suspend fun getAverageRating(driverId: String): Float {
         val rides = rideHistoryDao.getRideHistoryForUser(driverId)
             .firstOrNull() ?: return 0f
         
         val ratings = rides
             .filter { it.driver.id == driverId && it.rating != null }
             .mapNotNull { it.rating?.score }
         
         return if (ratings.isNotEmpty()) {
             ratings.average().toFloat()
         } else {
             0f
         }
     }
 }
