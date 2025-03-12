package com.example.indicab.data

import androidx.room.TypeConverter
import com.example.indicab.models.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.time.LocalDateTime
import java.time.ZoneOffset

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromTimestamp(value: Long?): LocalDateTime? {
        return value?.let { LocalDateTime.ofEpochSecond(it, 0, ZoneOffset.UTC) }
    }

    @TypeConverter
    fun dateToTimestamp(date: LocalDateTime?): Long? {
        return date?.toEpochSecond(ZoneOffset.UTC)
    }

    @TypeConverter
    fun fromUserProfile(value: UserProfile): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toUserProfile(value: String): UserProfile {
        return gson.fromJson(value, UserProfile::class.java)
    }

    @TypeConverter
    fun fromUserSettings(value: UserSettings): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toUserSettings(value: String): UserSettings {
        return gson.fromJson(value, UserSettings::class.java)
    }

    @TypeConverter
    fun fromPaymentMethodDetails(value: PaymentMethodDetails): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toPaymentMethodDetails(value: String): PaymentMethodDetails {
        return gson.fromJson(value, PaymentMethodDetails::class.java)
    }

    @TypeConverter
    fun fromTransactionMetadata(value: TransactionMetadata?): String? {
        return value?.let { gson.toJson(it) }
    }

    @TypeConverter
    fun toTransactionMetadata(value: String?): TransactionMetadata? {
        return value?.let { gson.fromJson(it, TransactionMetadata::class.java) }
    }

    @TypeConverter
    fun fromPaymentMethodType(value: PaymentMethodType): String {
        return value.name
    }

    @TypeConverter
    fun toPaymentMethodType(value: String): PaymentMethodType {
        return PaymentMethodType.valueOf(value)
    }

    @TypeConverter
    fun fromTransactionType(value: TransactionType): String {
        return value.name
    }

    @TypeConverter
    fun toTransactionType(value: String): TransactionType {
        return TransactionType.valueOf(value)
    }

    @TypeConverter
    fun fromTransactionStatus(value: TransactionStatus): String {
        return value.name
    }

    @TypeConverter
    fun toTransactionStatus(value: String): TransactionStatus {
        return TransactionStatus.valueOf(value)
    }

    @TypeConverter
    fun fromEmergencyContacts(value: List<EmergencyContact>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toEmergencyContacts(value: String): List<EmergencyContact> {
        val listType = object : TypeToken<List<EmergencyContact>>() {}.type
        return gson.fromJson(value, listType)
    }

    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(value, listType)
    }
}
