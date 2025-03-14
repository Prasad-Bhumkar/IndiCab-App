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
        // TODO: Check if default rating tags already exist before insertion; implement initialization logic accordingly.
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
        // TODO: Implement logic to monitor rating prompts, remove or mark expired ones, and notify users accordingly.
        serviceScope.launch {
            // Monitor and handle expired prompts
        }
    }

    suspend fun submitRating(
        // TODO: Validate rating data (score, review text, and tags) for proper format and acceptable ranges prior to submission.
        bookingId: String,
        fromUserId: String,
        toUserId: String,
        ratingType: RatingType,
        score: Float,
        review: String? = null,
        tags: List<String> = emptyList()
    ): Rating {
        // Create and save the rating
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

        // Update rating stats
        statsDao.updateRatingStats(toUserId, rating)

        // Update tag usage counts
        tags.forEach { tagId ->
            tagDao.incrementTagUsage(tagId)
        }

        // Mark rating prompt as completed
        promptDao.updatePromptStatusForBooking(
            bookingId = bookingId,
            userId = fromUserId,
            status = RatingPromptStatus.COMPLETED
        )

        return rating
    }

    suspend fun createRatingPrompt(
        // TODO: Validate bookingId and userId to ensure they are valid before creating a rating prompt.
        bookingId: String,
        userId: String,
        promptType: RatingPromptType
    ): RatingPrompt {
        val prompt = RatingPrompt(
            bookingId = bookingId,
            userId = userId,
            promptType = promptType
        )
        promptDao.insertPrompt(prompt)
        return prompt
    }

    suspend fun reportRating(
        // TODO: Add validation for report reason and description; ensure they are not empty and meet criteria.
        ratingId: String,
        reportedBy: String,
        reason: String,
        description: String? = null
    ): RatingReport {
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
        // TODO: Verify the existence of the report before moderating; handle missing report scenario gracefully.
        reportId: String,
        moderatorId: String,
        status: ReportStatus,
        notes: String? = null
    ) {
        reportDao.updateReportStatus(
            reportId = reportId,
            status = status,
            moderatorId = moderatorId,
            notes = notes
        )
    }

    suspend fun getRatingSummary(userId: String): RatingSummary {
        // TODO: Handle empty rating sets and log warning if no ratings are found.
        val stats = statsDao.getRatingStats(userId).firstOrNull()
        val recentRating = ratingDao.getRatingsForUser(userId)
            .firstOrNull()
            ?.firstOrNull()

        return RatingSummary(
            userId = userId,
            averageRating = stats?.averageRating ?: 0f,
            totalRatings = stats?.totalRatings ?: 0,
            recentRating = recentRating,
            topTags = stats?.commonTags ?: emptyList()
        )
    }

    fun getRatingAnalytics(
        // TODO: Add error handling for analytics calculation failures and unexpected data formats.
        userId: String,
        period: AnalyticsPeriod
    ): Flow<RatingAnalytics> = flow {
        // Calculate start and end dates based on period
        val now = LocalDateTime.now()
        val (startDate, endDate) = when (period) {
            AnalyticsPeriod.DAILY -> Pair(
                now.minusDays(1),
                now
            )
            AnalyticsPeriod.WEEKLY -> Pair(
                now.minusWeeks(1),
                now
            )
            AnalyticsPeriod.MONTHLY -> Pair(
                now.minusMonths(1),
                now
            )
            AnalyticsPeriod.YEARLY -> Pair(
                now.minusYears(1),
                now
            )
            AnalyticsPeriod.ALL_TIME -> Pair(
                LocalDateTime.MIN,
                now
            )
        }

        // Collect ratings for the period
        val ratings = ratingDao.getRatingsForUser(userId)
            .firstOrNull()
            ?.filter { it.createdAt in startDate..endDate }
            ?: emptyList()

        if (ratings.isEmpty()) {
            emit(
                RatingAnalytics(
                    userId = userId,
                    period = period,
                    averageRating = 0f,
                    ratingTrend = 0f,
                    totalRatings = 0,
                    topTags = emptyList(),
                    improvementAreas = emptyList()
                )
            )
            return@flow
        }

        // Calculate analytics
        val averageRating = ratings.map { it.score }.average().toFloat()
        
        // Calculate trend (compare with previous period)
        val previousPeriodRatings = ratingDao.getRatingsForUser(userId)
            .firstOrNull()
            ?.filter {
                it.createdAt in startDate.minus(period.toDuration())..endDate.minus(period.toDuration())
            }
            ?: emptyList()
        
        val previousAverage = previousPeriodRatings
            .takeIf { it.isNotEmpty() }
            ?.map { it.score }
            ?.average()
            ?.toFloat()
            ?: averageRating
        
        val trend = averageRating - previousAverage

        // Analyze tags
        val tagCounts = ratings
            .flatMap { it.tags }
            .groupBy { it }
            .mapValues { it.value.size }

        val totalTags = tagCounts.values.sum().toFloat()
        
        val topTags = tagCounts
            .map { (tag, count) ->
                TagCount(
                    tag = tag,
                    count = count,
                    percentage = count / totalTags * 100
                )
            }
            .sortedByDescending { it.count }
            .take(5)

        val improvementAreas = tagCounts
            .filter { (tag, _) ->
                DefaultRatingTags.NEGATIVE_TAGS.any { it.text == tag }
            }
            .map { (tag, count) ->
                TagCount(
                    tag = tag,
                    count = count,
                    percentage = count / totalTags * 100
                )
            }
            .sortedByDescending { it.count }
            .take(3)

        emit(
            RatingAnalytics(
                userId = userId,
                period = period,
                averageRating = averageRating,
                ratingTrend = trend,
                totalRatings = ratings.size,
                topTags = topTags,
                improvementAreas = improvementAreas
            )
        )
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
