 package com.example.indicab.data.dao
 
 import androidx.room.*
 import com.example.indicab.models.*
 import kotlinx.coroutines.flow.Flow
 import java.time.LocalDateTime
 
 @Dao
 interface RatingDao {
     @Query("SELECT * FROM ratings WHERE toUserId = :userId ORDER BY createdAt DESC")
     fun getRatingsForUser(userId: String): Flow<List<Rating>>
 
     @Query("""
         SELECT * FROM ratings 
         WHERE bookingId = :bookingId 
         AND ratingType = :type 
         LIMIT 1
     """)
     suspend fun getRatingForBooking(bookingId: String, type: RatingType): Rating?
 
     @Insert(onConflict = OnConflictStrategy.REPLACE)
     suspend fun insertRating(rating: Rating)
 
     @Update
     suspend fun updateRating(rating: Rating)
 
     @Query("""
         UPDATE ratings 
         SET status = :status,
             updatedAt = :timestamp
         WHERE id = :ratingId
     """)
     suspend fun updateRatingStatus(
         ratingId: String,
         status: RatingStatus,
         timestamp: LocalDateTime = LocalDateTime.now()
     )
 
     @Query("""
         SELECT AVG(score) 
         FROM ratings 
         WHERE toUserId = :userId 
         AND status = :status
     """)
     fun getAverageRating(
         userId: String,
         status: RatingStatus = RatingStatus.ACTIVE
     ): Flow<Float?>
 
     @Query("""
         SELECT COUNT(*) 
         FROM ratings 
         WHERE toUserId = :userId 
         AND status = :status
     """)
     fun getRatingCount(
         userId: String,
         status: RatingStatus = RatingStatus.ACTIVE
     ): Flow<Int>
 }
 
 @Dao
 interface RatingPromptDao {
     @Query("""
         SELECT * FROM rating_prompts 
         WHERE userId = :userId 
         AND status = :status 
         AND expiresAt > :now
         ORDER BY createdAt DESC
     """)
     fun getPendingPrompts(
         userId: String,
         status: RatingPromptStatus = RatingPromptStatus.PENDING,
         now: LocalDateTime = LocalDateTime.now()
     ): Flow<List<RatingPrompt>>
 
     @Insert(onConflict = OnConflictStrategy.REPLACE)
     suspend fun insertPrompt(prompt: RatingPrompt)
 
     @Update
     suspend fun updatePrompt(prompt: RatingPrompt)
 
     @Query("""
         UPDATE rating_prompts 
         SET status = :status,
             updatedAt = :timestamp
         WHERE id = :promptId
     """)
     suspend fun updatePromptStatus(
         promptId: String,
         status: RatingPromptStatus,
         timestamp: LocalDateTime = LocalDateTime.now()
     )
 
     @Query("""
         UPDATE rating_prompts 
         SET status = :status,
             updatedAt = :timestamp
         WHERE bookingId = :bookingId 
         AND userId = :userId
     """)
     suspend fun updatePromptStatusForBooking(
         bookingId: String,
         userId: String,
         status: RatingPromptStatus,
         timestamp: LocalDateTime = LocalDateTime.now()
     )
 }
 
 @Dao
 interface RatingTagDao {
     @Query("SELECT * FROM rating_tags WHERE isActive = 1 ORDER BY usageCount DESC")
     fun getAllTags(): Flow<List<RatingTag>>
 
     @Query("""
         SELECT * FROM rating_tags 
         WHERE type = :type 
         AND isActive = 1 
         ORDER BY usageCount DESC
     """)
     fun getTagsByType(type: RatingTagType): Flow<List<RatingTag>>
 
     @Query("""
         SELECT * FROM rating_tags 
         WHERE category = :category 
         AND isActive = 1 
         ORDER BY usageCount DESC
     """)
     fun getTagsByCategory(category: RatingTagCategory): Flow<List<RatingTag>>
 
     @Insert(onConflict = OnConflictStrategy.REPLACE)
     suspend fun insertTag(tag: RatingTag)
 
     @Update
     suspend fun updateTag(tag: RatingTag)
 
     @Query("""
         UPDATE rating_tags 
         SET usageCount = usageCount + 1,
             updatedAt = :timestamp
         WHERE id = :tagId
     """)
     suspend fun incrementTagUsage(
         tagId: String,
         timestamp: LocalDateTime = LocalDateTime.now()
     )
 }
 
 @Dao
 interface RatingStatsDao {
     @Query("SELECT * FROM rating_stats WHERE userId = :userId")
     fun getRatingStats(userId: String): Flow<RatingStats?>
 
     @Insert(onConflict = OnConflictStrategy.REPLACE)
     suspend fun insertStats(stats: RatingStats)
 
     @Update
     suspend fun updateStats(stats: RatingStats)
 
     @Transaction
     suspend fun updateRatingStats(userId: String, rating: Rating) {
         val currentStats = getRatingStats(userId).value ?: RatingStats(userId)
         
         val newTotalRatings = currentStats.totalRatings + 1
         val newAverage = (currentStats.averageRating * currentStats.totalRatings + rating.score) / newTotalRatings
         
         val distribution = currentStats.ratingDistribution.toMutableMap()
         val score = rating.score.toInt()
         distribution[score] = (distribution[score] ?: 0) + 1
 
         val updatedStats = currentStats.copy(
             averageRating = newAverage,
             totalRatings = newTotalRatings,
             ratingDistribution = distribution,
             commonTags = updateCommonTags(currentStats.commonTags, rating.tags),
             lastUpdated = LocalDateTime.now()
         )
 
         insertStats(updatedStats)
     }
 
     private fun updateCommonTags(
         currentTags: List<String>,
         newTags: List<String>,
         maxTags: Int = 10
     ): List<String> {
         val tagCounts = (currentTags + newTags)
             .groupBy { it }
             .mapValues { it.value.size }
             .toList()
             .sortedByDescending { it.second }
             .take(maxTags)
             .map { it.first }
         return tagCounts
     }
 }
 
 @Dao
 interface RatingReportDao {
     @Query("SELECT * FROM rating_reports WHERE status = :status ORDER BY createdAt DESC")
     fun getPendingReports(status: ReportStatus = ReportStatus.PENDING): Flow<List<RatingReport>>
 
     @Query("SELECT * FROM rating_reports WHERE ratingId = :ratingId")
     fun getReportsForRating(ratingId: String): Flow<List<RatingReport>>
 
     @Insert(onConflict = OnConflictStrategy.REPLACE)
     suspend fun insertReport(report: RatingReport)
 
     @Update
     suspend fun updateReport(report: RatingReport)
 
     @Query("""
         UPDATE rating_reports 
         SET status = :status,
             moderatorId = :moderatorId,
             moderatorNotes = :notes,
             updatedAt = :timestamp
         WHERE id = :reportId
     """)
     suspend fun updateReportStatus(
         reportId: String,
         status: ReportStatus,
         moderatorId: String?,
         notes: String?,
         timestamp: LocalDateTime = LocalDateTime.now()
     )
 }
