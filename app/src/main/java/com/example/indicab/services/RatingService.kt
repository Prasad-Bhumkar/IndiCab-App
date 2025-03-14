package com.example.indicab.services

import com.example.indicab.data.dao.*
import com.example.indicab.models.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton
import android.util.Log

@Singleton
class RatingService @Inject constructor(
    private val ratingDao: RatingDao,
    private val promptDao: RatingPromptDao,
    private val tagDao: RatingTagDao,
    private val statsDao: RatingStatsDao,
    private val reportDao: RatingReportDao
) {
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    init {
        initializeDefaultTags()
        monitorExpiredPrompts()
    }

    private fun initializeDefaultTags() {
        serviceScope.launch {
            DefaultRatingTags.POSITIVE_TAGS.forEach { tag ->
                tagDao.insertTag(tag)
            }
            DefaultRatingTags.NEGATIVE_TAGS.forEach { tag ->
                tagDao.insertTag(tag)
            }
        }
    }

    private fun monitorExpiredPrompts() {
        serviceScope.launch {
            // TODO: Implement logic to monitor and handle expired rating prompts (e.g., automatically mark prompts as expired and notify users).
        }
    }

    suspend fun submitRating(
        bookingId: String,
        fromUserId: String,
        toUserId: String,
        ratingType: RatingType,
        score: Float,
        review: String? = null,
        tags: List<String> = emptyList()
    ): Rating {
        require(score in 1.0..5.0) { "Score must be between 1 and 5." }
        require(tags.all { it.isNotBlank() }) { "Tags cannot be empty." }
        require(bookingId.isNotBlank()) { "Booking ID cannot be empty." }
        require(fromUserId.isNotBlank()) { "From User ID cannot be empty." }
        require(toUserId.isNotBlank()) { "To User ID cannot be empty." }
        // TODO: Enhance validation logic (e.g., check for duplicate ratings) before inserting a new rating.

        val rating = Rating(
            bookingId = bookingId,
            fromUserId = fromUserId,
            toUserId = toUserId,
            ratingType = ratingType,
            score = score,
            review = review,
            tags = tags
        )
        ratingDao.insertRating(rating)

        statsDao.updateRatingStats(toUserId, rating)

        tags.forEach { tagId ->
            tagDao.incrementTagUsage(tagId)
        }

        promptDao.updatePromptStatusForBooking(
            bookingId = bookingId,
            userId = fromUserId,
            status = RatingPromptStatus.COMPLETED
        )

        return rating
    }

    suspend fun createRatingPrompt(
        bookingId: String,
        userId: String,
        promptType: RatingPromptType
    ): RatingPrompt {
        require(bookingId.isNotBlank()) { "Booking ID cannot be empty." }
        require(userId.isNotBlank()) { "User ID cannot be empty." }
        // TODO: Validate prompt data and ensure all required fields are present before insertion.

        val prompt = RatingPrompt(
            bookingId = bookingId,
            userId = userId,
            promptType = promptType
        )
        promptDao.insertPrompt(prompt)
        return prompt
    }

    suspend fun reportRating(
        ratingId: String,
        reportedBy: String,
        reason: String,
        description: String? = null
    ): RatingReport {
        require(reason.isNotBlank()) { "Reason cannot be empty." }
        require(description?.isNotBlank() ?: true) { "Description cannot be empty." }
        // TODO: Add error handling to verify that the rating exists before reporting; handle invalid rating IDs.

        val report = RatingReport(
            ratingId = ratingId,
            reportedBy = reportedBy,
            reason = reason,
            description = description
        )
        reportDao.insertReport(report)
        return report
    }

    suspend fun moderateReport(
        reportId: String,
        moderatorId: String,
        status: ReportStatus,
        notes: String? = null
    ) {
        require(reportId.isNotBlank()) { "Report ID cannot be empty." }
        // TODO: Validate the presence and current state of the report before processing moderation actions.
        reportDao.updateReportStatus(
            reportId = reportId,
            status = status,
            moderatorId = moderatorId,
            notes = notes
        )
    }

    suspend fun getRatingSummary(userId: String): RatingSummary {
        val stats = statsDao.getRatingStats(userId).firstOrNull() ?: run {
            Log.w("RatingService", "No rating stats found for userId=$userId")
            // TODO: Log detailed information when no rating stats are returned to aid in debugging and monitoring.
            return RatingSummary(userId, 0f, 0, null, emptyList())
        }
        val recentRating = ratingDao.getRatingsForUser(userId)
            .firstOrNull()
            ?.firstOrNull()

        return RatingSummary(
            userId = userId,
            averageRating = stats.averageRating,
            totalRatings = stats.totalRatings,
            recentRating = recentRating,
            topTags = stats.commonTags
        )
    }

    fun getRatingAnalytics(
        userId: String,
        period: AnalyticsPeriod
    ): Flow<RatingAnalytics> = flow {
        require(userId.isNotBlank()) { "User ID cannot be empty." }

        val now = LocalDateTime.now()
        val (startDate, endDate) = when (period) {
            AnalyticsPeriod.DAILY -> Pair(now.minusDays(1), now)
            AnalyticsPeriod.WEEKLY -> Pair(now.minusWeeks(1), now)
            AnalyticsPeriod.MONTHLY -> Pair(now.minusMonths(1), now)
            AnalyticsPeriod.YEARLY -> Pair(now.minusYears(1), now)
            AnalyticsPeriod.ALL_TIME -> Pair(LocalDateTime.MIN, now)
        }

        val ratings = ratingDao.getRatingsForUser(userId)
            .firstOrNull()
            ?.filter { it.createdAt in startDate..endDate }
            ?: emptyList()

        if (ratings.isEmpty()) {
            emit(RatingAnalytics(userId, period, 0f, 0f, 0, emptyList(), emptyList()))
            return@flow
        }

        val averageRating = ratings.map { it.score }.average().toFloat()
        val previousPeriodRatings = ratingDao.getRatingsForUser(userId)
            .firstOrNull()
            ?.filter {
                it.createdAt in startDate.minus(period.toDuration())..endDate.minus(period.toDuration())
            }
            ?: emptyList()

        val previousAverage = previousPeriodRatings.takeIf { it.isNotEmpty() }
            ?.map { it.score }
            ?.average()
            ?.toFloat() ?: averageRating

        val trend = averageRating - previousAverage

        val tagCounts = ratings.flatMap { it.tags }.groupBy { it }.mapValues { it.value.size }
        val totalTags = tagCounts.values.sum().toFloat()

        val topTags = tagCounts.map { (tag, count) ->
            TagCount(tag, count, count / totalTags * 100)
        }.sortedByDescending { it.count }.take(5)

        val improvementAreas = tagCounts.filter { (tag, _) ->
            DefaultRatingTags.NEGATIVE_TAGS.any { it.text == tag }
        }.map { (tag, count) ->
            TagCount(tag, count, count / totalTags * 100)
        }.sortedByDescending { it.count }.take(3)

        emit(RatingAnalytics(userId, period, averageRating, trend, ratings.size, topTags, improvementAreas))
    }

    private fun AnalyticsPeriod.toDuration() = when (this) {
        AnalyticsPeriod.DAILY -> java.time.Duration.ofDays(1)
        AnalyticsPeriod.WEEKLY -> java.time.Duration.ofDays(7)
        AnalyticsPeriod.MONTHLY -> java.time.Duration.ofDays(30)
        AnalyticsPeriod.YEARLY -> java.time.Duration.ofDays(365)
        AnalyticsPeriod.ALL_TIME -> java.time.Duration.ZERO
    }

    fun getPendingRatingPrompts(userId: String): Flow<List<RatingPrompt>> =
        promptDao.getPendingPrompts(userId)

    fun getAvailableTags(type: RatingTagType? = null): Flow<List<RatingTag>> =
        if (type != null) {
            tagDao.getTagsByType(type)
        } else {
            tagDao.getAllTags()
        }

    fun getPendingReports(): Flow<List<RatingReport>> =
        reportDao.getPendingReports()
}
