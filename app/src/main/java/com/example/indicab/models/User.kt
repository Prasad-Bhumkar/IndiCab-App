package com.example.indicab.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.indicab.data.Converters
import java.time.LocalDateTime

@Entity(tableName = "users")
@TypeConverters(Converters::class)
data class User(
    @PrimaryKey
    val id: String = "USR" + System.currentTimeMillis(),
    val email: String,
    val phoneNumber: String,
    val name: String,
    val profile: UserProfile = UserProfile(),
    val settings: UserSettings = UserSettings(),
    val isActive: Boolean = true,
    val isVerified: Boolean = false,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now(),
    val lastLoginAt: LocalDateTime? = null,
    val deviceToken: String? = null
)

data class UserProfile(
    val avatar: String = "",
    val dateOfBirth: LocalDateTime? = null,
    val gender: String = "",
    val address: String = "",
    val city: String = "",
    val state: String = "",
    val country: String = "",
    val pinCode: String = "",
    val emergencyContacts: List<EmergencyContact> = emptyList(),
    val preferredLanguage: String = "en",
    val bio: String = ""
)

data class UserSettings(
    val notifications: NotificationSettings = NotificationSettings(),
    val privacySettings: PrivacySettings = PrivacySettings(),
    val displaySettings: DisplaySettings = DisplaySettings(),
    val paymentSettings: PaymentSettings = PaymentSettings()
)

data class NotificationSettings(
    val pushEnabled: Boolean = true,
    val emailEnabled: Boolean = true,
    val smsEnabled: Boolean = true,
    val rideUpdates: Boolean = true,
    val promotions: Boolean = true,
    val paymentAlerts: Boolean = true
)

data class PrivacySettings(
    val shareLocation: Boolean = true,
    val shareRideHistory: Boolean = false,
    val shareProfile: Boolean = true,
    val showOnlineStatus: Boolean = true
)

data class DisplaySettings(
    val theme: String = "system",
    val fontSize: Int = 16,
    val useHighContrast: Boolean = false,
    val mapStyle: String = "standard"
)

data class PaymentSettings(
    val autoRecharge: Boolean = false,
    val rechargeThreshold: Double = 100.0,
    val rechargeAmount: Double = 500.0,
    val defaultPaymentMethod: String? = null,
    val enableSplitPayments: Boolean = true
)

data class EmergencyContact(
    val name: String,
    val phoneNumber: String,
    val relationship: String,
    val isDefault: Boolean = false
)
