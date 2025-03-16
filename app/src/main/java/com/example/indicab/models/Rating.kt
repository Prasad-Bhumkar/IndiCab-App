 package com.example.indicab.models
 
 import androidx.room.Entity
 import androidx.room.PrimaryKey
 import java.time.LocalDateTime
 
 @Entity(tableName = "ratings")
 data class Rating(
     @PrimaryKey
     val id: String = "RAT" + System.currentTimeMillis(),
     val bookingId: String,
     val fromUserId: String,
     val toUserId: String,
     val ratingType: RatingType,
     val score: Float,
     val review: String? = null,
     val tags: List<String> = emptyList(),
     val status: RatingStatus = RatingStatus.ACTIVE,
     val metadata: RatingMetadata? = null,
     val createdAt: LocalDateTime = LocalDateTime.now(),
     val updatedAt: LocalDateTime = LocalDateTime.now()
 )
 
 enum class RatingType {
     RIDER_TO_DRIVER,
     DRIVER_TO_RIDER
 }
 
 enum class RatingStatus {
     ACTIVE,     // Rating is visible and counted
     HIDDEN,     // Rating is hidden but counted
     REPORTED,   // Rating has been reported
     REMOVED     // Rating is not counted
 }
 
 data class RatingMetadata(
     val rideDistance: Double? = null,
     val rideDuration: Long? = null,
     val rideType: String? = null,
     val vehicleType: String? = null,
     val reportReason: String? = null,
     val moderatorNotes: String? = null
 )
 
 @Entity(tableName = "rating_prompts")
 data class RatingPrompt(
     @PrimaryKey
     val id: String = "PRM" + System.currentTimeMillis(),
     val bookingId: String,
     val userId: String,
     val promptType: RatingPromptType,
     val status: RatingPromptStatus = RatingPromptStatus.PENDING,
     val expiresAt: LocalDateTime = LocalDateTime.now().plusDays(7),
     val createdAt: LocalDateTime = LocalDateTime.now(),
     val updatedAt: LocalDateTime = LocalDateTime.now()
 )
 
 enum class RatingPromptType {
     RIDER_RATING,
     DRIVER_RATING
 }
 
 enum class RatingPromptStatus {
     PENDING,    // Rating not submitted yet
     COMPLETED,  // Rating submitted
     SKIPPED,    // User skipped rating
     EXPIRED     // Time window expired
 }
 
 @Entity(tableName = "rating_tags")
 data class RatingTag(
     @PrimaryKey
     val id: String = "TAG" + System.currentTimeMillis(),
     val text: String,
     val type: RatingTagType,
     val category: RatingTagCategory,
     val isActive: Boolean = true,
     val usageCount: Int = 0,
     val createdAt: LocalDateTime = LocalDateTime.now(),
     val updatedAt: LocalDateTime = LocalDateTime.now()
 )
 
 enum class RatingTagType {
     POSITIVE,   // Positive feedback
     NEGATIVE,   // Negative feedback
     NEUTRAL     // Neutral feedback
 }
 
 enum class RatingTagCategory {
     DRIVING,        // Driving skills
     BEHAVIOR,       // Personal behavior
     VEHICLE,        // Vehicle condition
     CLEANLINESS,   // Cleanliness
     PUNCTUALITY,   // Time management
     NAVIGATION,    // Route knowledge
     SAFETY,        // Safety measures
     COMMUNICATION  // Communication skills
 }
 
 // Aggregate rating statistics
 @Entity(tableName = "rating_stats")
 data class RatingStats(
     @PrimaryKey
     val userId: String,
     val averageRating: Float = 0f,
     val totalRatings: Int = 0,
     val ratingDistribution: Map<Int, Int> = emptyMap(), // Map of rating score to count
     val commonTags: List<String> = emptyList(),
     val lastUpdated: LocalDateTime = LocalDateTime.now()
 )
 
 // Rating report for moderation
 @Entity(tableName = "rating_reports")
 data class RatingReport(
     @PrimaryKey
     val id: String = "REP" + System.currentTimeMillis(),
     val ratingId: String,
     val reportedBy: String,
     val reason: String,
     val description: String? = null,
     val status: ReportStatus = ReportStatus.PENDING,
     val moderatorId: String? = null,
     val moderatorNotes: String? = null,
     val createdAt: LocalDateTime = LocalDateTime.now(),
     val updatedAt: LocalDateTime = LocalDateTime.now()
 )
 
 enum class ReportStatus {
     PENDING,    // Report needs review
     REVIEWING,  // Under moderation review
     ACCEPTED,   // Report accepted, rating hidden
     REJECTED,   // Report rejected, rating remains
     RESOLVED    // Issue resolved
 }
 
 // Rating analytics data
 data class RatingAnalytics(
     val userId: String,
     val period: AnalyticsPeriod,
     val averageRating: Float,
     val ratingTrend: Float, // Change from previous period
     val totalRatings: Int,
     val topTags: List<TagCount>,
     val improvementAreas: List<TagCount>,
     val timestamp: LocalDateTime = LocalDateTime.now()
 )
 
 enum class AnalyticsPeriod {
     DAILY,
     WEEKLY,
     MONTHLY,
     YEARLY,
     ALL_TIME
 }
 
 data class TagCount(
     val tag: String,
     val count: Int,
     val percentage: Float
 )
 
 // Rating summary for quick display
 data class RatingSummary(
     val userId: String,
     val averageRating: Float,
     val totalRatings: Int,
     val recentRating: Rating? = null,
     val topTags: List<String> = emptyList()
 )
 
 // Default rating tags
 object DefaultRatingTags {
     val POSITIVE_TAGS = listOf(
         RatingTag(text = "Great driving", type = RatingTagType.POSITIVE, category = RatingTagCategory.DRIVING),
         RatingTag(text = "Very punctual", type = RatingTagType.POSITIVE, category = RatingTagCategory.PUNCTUALITY),
         RatingTag(text = "Clean vehicle", type = RatingTagType.POSITIVE, category = RatingTagCategory.CLEANLINESS),
         RatingTag(text = "Professional", type = RatingTagType.POSITIVE, category = RatingTagCategory.BEHAVIOR),
         RatingTag(text = "Safe driver", type = RatingTagType.POSITIVE, category = RatingTagCategory.SAFETY),
         RatingTag(text = "Great route", type = RatingTagType.POSITIVE, category = RatingTagCategory.NAVIGATION),
         RatingTag(text = "Excellent communication", type = RatingTagType.POSITIVE, category = RatingTagCategory.COMMUNICATION)
     )
 
     val NEGATIVE_TAGS = listOf(
         RatingTag(text = "Poor driving", type = RatingTagType.NEGATIVE, category = RatingTagCategory.DRIVING),
         RatingTag(text = "Late arrival", type = RatingTagType.NEGATIVE, category = RatingTagCategory.PUNCTUALITY),
         RatingTag(text = "Dirty vehicle", type = RatingTagType.NEGATIVE, category = RatingTagCategory.CLEANLINESS),
         RatingTag(text = "Unprofessional", type = RatingTagType.NEGATIVE, category = RatingTagCategory.BEHAVIOR),
         RatingTag(text = "Unsafe driving", type = RatingTagType.NEGATIVE, category = RatingTagCategory.SAFETY),
         RatingTag(text = "Wrong route", type = RatingTagType.NEGATIVE, category = RatingTagCategory.NAVIGATION),
         RatingTag(text = "Poor communication", type = RatingTagType.NEGATIVE, category = RatingTagCategory.COMMUNICATION)
     )
 }
