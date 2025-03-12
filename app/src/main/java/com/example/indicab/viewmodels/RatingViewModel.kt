package com.example.indicab.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.indicab.models.*
import com.example.indicab.services.RatingService
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class RatingViewModel(
    private val ratingService: RatingService,
    private val userId: String,
    private val bookingId: String? = null
) : ViewModel() {

    private val _ratingState = MutableStateFlow<RatingState>(RatingState.Initial)
    val ratingState = _ratingState.asStateFlow()

    private val _selectedTags = MutableStateFlow<Set<String>>(emptySet())
    val selectedTags = _selectedTags.asStateFlow()

    private val _ratingScore = MutableStateFlow<Float?>(null)
    val ratingScore = _ratingScore.asStateFlow()

    private val _review = MutableStateFlow("")
    val review = _review.asStateFlow()

    val positiveTags = ratingService.getAvailableTags(RatingTagType.POSITIVE)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val negativeTags = ratingService.getAvailableTags(RatingTagType.NEGATIVE)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val pendingPrompts = ratingService.getPendingRatingPrompts(userId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _ratingAnalytics = MutableStateFlow<RatingAnalytics?>(null)
    val ratingAnalytics = _ratingAnalytics.asStateFlow()

    init {
        loadRatingAnalytics()
    }

    private fun loadRatingAnalytics() {
        viewModelScope.launch {
            try {
                ratingService.getRatingAnalytics(userId, AnalyticsPeriod.MONTHLY)
                    .collect { analytics ->
                        _ratingAnalytics.value = analytics
                    }
            } catch (e: Exception) {
                _ratingState.value = RatingState.Error(
                    e.message ?: "Failed to load rating analytics"
                )
            }
        }
    }

    fun setRatingScore(score: Float) {
        _ratingScore.value = score
    }

    fun setReview(text: String) {
        _review.value = text
    }

    fun toggleTag(tag: String) {
        _selectedTags.update { currentTags ->
            if (currentTags.contains(tag)) {
                currentTags - tag
            } else {
                currentTags + tag
            }
        }
    }

    fun submitRating(toUserId: String, ratingType: RatingType) {
        viewModelScope.launch {
            try {
                _ratingState.value = RatingState.Submitting

                val score = _ratingScore.value
                    ?: throw IllegalStateException("Rating score not set")

                val bookingId = this@RatingViewModel.bookingId
                    ?: throw IllegalStateException("Booking ID not available")

                val rating = ratingService.submitRating(
                    bookingId = bookingId,
                    fromUserId = userId,
                    toUserId = toUserId,
                    ratingType = ratingType,
                    score = score,
                    review = _review.value.takeIf { it.isNotBlank() },
                    tags = _selectedTags.value.toList()
                )

                _ratingState.value = RatingState.Success(rating)
            } catch (e: Exception) {
                _ratingState.value = RatingState.Error(
                    e.message ?: "Failed to submit rating"
                )
            }
        }
    }

    fun reportRating(
        ratingId: String,
        reason: String,
        description: String? = null
    ) {
        viewModelScope.launch {
            try {
                _ratingState.value = RatingState.Submitting

                val report = ratingService.reportRating(
                    ratingId = ratingId,
                    reportedBy = userId,
                    reason = reason,
                    description = description
                )

                _ratingState.value = RatingState.ReportSubmitted(report)
            } catch (e: Exception) {
                _ratingState.value = RatingState.Error(
                    e.message ?: "Failed to submit report"
                )
            }
        }
    }

    fun skipRating(promptId: String) {
        viewModelScope.launch {
            try {
                // Mark the prompt as skipped
                // This would be implemented in the RatingService
                _ratingState.value = RatingState.Skipped
            } catch (e: Exception) {
                _ratingState.value = RatingState.Error(
                    e.message ?: "Failed to skip rating"
                )
            }
        }
    }

    fun resetState() {
        _ratingState.value = RatingState.Initial
        _selectedTags.value = emptySet()
        _ratingScore.value = null
        _review.value = ""
    }

    class Factory(
        private val ratingService: RatingService,
        private val userId: String,
        private val bookingId: String? = null
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(RatingViewModel::class.java)) {
                return RatingViewModel(ratingService, userId, bookingId) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

sealed class RatingState {
    object Initial : RatingState()
    object Submitting : RatingState()
    object Skipped : RatingState()
    data class Success(val rating: Rating) : RatingState()
    data class ReportSubmitted(val report: RatingReport) : RatingState()
    data class Error(val message: String) : RatingState()
}
