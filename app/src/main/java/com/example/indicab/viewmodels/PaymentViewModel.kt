package com.example.indicab.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.indicab.models.*
import com.example.indicab.services.PaymentService
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class PaymentViewModel(
    private val paymentService: PaymentService,
    private val userId: String // TODO: Get from UserManager
) : ViewModel() {

    private val _paymentState = MutableStateFlow<PaymentState>(PaymentState.Initial)
    val paymentState = _paymentState.asStateFlow()

    private val _selectedPaymentMethod = MutableStateFlow<PaymentMethod?>(null)
    val selectedPaymentMethod = _selectedPaymentMethod.asStateFlow()

    val activePaymentMethods = paymentService.getActivePaymentMethods(userId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val wallet = paymentService.getWalletBalance(userId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val recentTransactions = paymentService.getTransactionHistory(userId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun processPayment(
        amount: Double,
        bookingId: String? = null,
        splitDetails: SplitPaymentDetails? = null
    ) {
        viewModelScope.launch {
            try {
                val paymentMethod = selectedPaymentMethod.value
                    ?: throw IllegalStateException("No payment method selected")

                _paymentState.value = PaymentState.Processing

                val request = PaymentRequest(
                    amount = amount,
                    paymentMethodId = paymentMethod.id,
                    bookingId = bookingId,
                    isSplitPayment = splitDetails != null,
                    splitDetails = splitDetails
                )

                val response = paymentService.processPayment(request)
                // Log the payment processing event
                AnalyticsManager.logEvent("payment_processing", mapOf("userId" to request.userId, "amount" to request.amount.toString()))
                
                _paymentState.value = if (response.success) {
                    PaymentState.Success(response)
                    // Log the payment result
                    AnalyticsManager.logEvent("payment_result", mapOf("userId" to request.userId, "success" to response.success.toString()))
                } else {
                    PaymentState.Error(
                        response.errorMessage ?: "Payment failed",
                        response.errorCode
                    )
                }
            } catch (e: Exception) {
                _paymentState.value = PaymentState.Error(
                    e.message ?: "Payment failed",
                    "UNKNOWN_ERROR"
                )
                // Log the payment failure
                AnalyticsManager.logEvent("payment_failed", mapOf("userId" to userId, "error" to e.message ?: "Unknown error"))
                    e.message ?: "Payment failed",
                    "UNKNOWN_ERROR"
                )
            }
        }
    }

    fun addPaymentMethod(
        type: PaymentMethodType,
        name: String,
        details: PaymentMethodDetails,
        setAsDefault: Boolean = false
    ) {
        viewModelScope.launch {
            try {
                _paymentState.value = PaymentState.Processing
                val paymentMethod = paymentService.addPaymentMethod(
                    userId = userId,
                    type = type,
                    name = name,
                    details = details,
                    setAsDefault = setAsDefault
                )
                if (setAsDefault) {
                    _selectedPaymentMethod.value = paymentMethod
                }
                _paymentState.value = PaymentState.PaymentMethodAdded(paymentMethod)
            } catch (e: Exception) {
                _paymentState.value = PaymentState.Error(
                    e.message ?: "Failed to add payment method",
                    "ADD_PAYMENT_METHOD_FAILED"
                )
            }
        }
    }

    fun removePaymentMethod(paymentMethod: PaymentMethod) {
        viewModelScope.launch {
            try {
                _paymentState.value = PaymentState.Processing
                paymentService.removePaymentMethod(paymentMethod)
                if (_selectedPaymentMethod.value?.id == paymentMethod.id) {
                    _selectedPaymentMethod.value = null
                }
                _paymentState.value = PaymentState.PaymentMethodRemoved(paymentMethod)
            } catch (e: Exception) {
                _paymentState.value = PaymentState.Error(
                    e.message ?: "Failed to remove payment method",
                    "REMOVE_PAYMENT_METHOD_FAILED"
                )
            }
        }
    }

    fun setDefaultPaymentMethod(paymentMethodId: String) {
        viewModelScope.launch {
            try {
                paymentService.setDefaultPaymentMethod(userId, paymentMethodId)
                val updatedMethods = activePaymentMethods.value
                _selectedPaymentMethod.value = updatedMethods.find { it.id == paymentMethodId }
            } catch (e: Exception) {
                _paymentState.value = PaymentState.Error(
                    e.message ?: "Failed to set default payment method",
                    "SET_DEFAULT_FAILED"
                )
            }
        }
    }

    fun selectPaymentMethod(paymentMethod: PaymentMethod?) {
        _selectedPaymentMethod.value = paymentMethod
    }

    fun resetState() {
        _paymentState.value = PaymentState.Initial
    }

    class Factory(
        private val paymentService: PaymentService,
        private val userId: String
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(PaymentViewModel::class.java)) {
                return PaymentViewModel(paymentService, userId) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

sealed class PaymentState {
    object Initial : PaymentState()
    object Processing : PaymentState()
    data class Success(val response: PaymentGatewayResponse) : PaymentState()
    data class Error(val message: String, val code: String? = null) : PaymentState()
    data class PaymentMethodAdded(val paymentMethod: PaymentMethod) : PaymentState()
    data class PaymentMethodRemoved(val paymentMethod: PaymentMethod) : PaymentState()
}
