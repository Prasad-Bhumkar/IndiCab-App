package com.example.indicab.data.dao

import androidx.room.*
import com.example.indicab.models.PaymentMethod
import kotlinx.coroutines.flow.Flow

@Dao
interface PaymentMethodDao {
    @Query("SELECT * FROM payment_methods WHERE userId = :userId AND isEnabled = 1")
    fun getActivePaymentMethods(userId: String): Flow<List<PaymentMethod>>

    @Query("SELECT * FROM payment_methods WHERE id = :id AND userId = :userId")
    suspend fun getPaymentMethodById(id: String, userId: String): PaymentMethod?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPaymentMethod(paymentMethod: PaymentMethod)

    @Update
    suspend fun updatePaymentMethod(paymentMethod: PaymentMethod)

    @Delete
    suspend fun deletePaymentMethod(paymentMethod: PaymentMethod)

    @Query("UPDATE payment_methods SET isDefault = (id = :paymentMethodId) WHERE userId = :userId")
    suspend fun setDefaultPaymentMethod(userId: String, paymentMethodId: String)

    @Query("SELECT * FROM payment_methods WHERE userId = :userId AND isDefault = 1 LIMIT 1")
    fun getDefaultPaymentMethod(userId: String): Flow<PaymentMethod?>

    @Query("SELECT * FROM payment_methods WHERE userId = :userId AND type = :type AND isEnabled = 1")
    fun getPaymentMethodsByType(userId: String, type: String): Flow<List<PaymentMethod>>

    @Query("UPDATE payment_methods SET isEnabled = 0 WHERE id = :id AND userId = :userId")
    suspend fun disablePaymentMethod(id: String, userId: String)

    @Transaction
    suspend fun addOrUpdatePaymentMethod(paymentMethod: PaymentMethod) {
        val existing = getPaymentMethodById(paymentMethod.id, paymentMethod.userId)
        if (existing != null) {
            updatePaymentMethod(paymentMethod)
        } else {
            insertPaymentMethod(paymentMethod)
        }
    }
}
