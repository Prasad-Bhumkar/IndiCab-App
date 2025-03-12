package com.example.indicab.data.dao

import androidx.room.*
import com.example.indicab.models.User
import com.example.indicab.models.UserProfile
import com.example.indicab.models.UserSettings
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE id = :userId")
    fun getUser(userId: String): Flow<User?>

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): User?

    @Query("SELECT * FROM users WHERE phoneNumber = :phoneNumber LIMIT 1")
    suspend fun getUserByPhone(phoneNumber: String): User?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Update
    suspend fun updateUser(user: User)

    @Delete
    suspend fun deleteUser(user: User)

    @Query("SELECT profile FROM users WHERE id = :userId")
    fun getUserProfile(userId: String): Flow<UserProfile?>

    @Query("UPDATE users SET profile = :profile WHERE id = :userId")
    suspend fun updateUserProfile(userId: String, profile: UserProfile)

    @Query("SELECT settings FROM users WHERE id = :userId")
    fun getUserSettings(userId: String): Flow<UserSettings?>

    @Query("UPDATE users SET settings = :settings WHERE id = :userId")
    suspend fun updateUserSettings(userId: String, settings: UserSettings)

    @Query("UPDATE users SET isVerified = 1 WHERE id = :userId")
    suspend fun markUserAsVerified(userId: String)

    @Query("UPDATE users SET lastLoginAt = :timestamp WHERE id = :userId")
    suspend fun updateLastLogin(userId: String, timestamp: Long = System.currentTimeMillis())

    @Query("""
        SELECT * FROM users 
        WHERE isActive = 1 
        AND (name LIKE '%' || :query || '%' OR email LIKE '%' || :query || '%')
    """)
    fun searchUsers(query: String): Flow<List<User>>

    @Transaction
    suspend fun createOrUpdateUser(user: User) {
        val existing = getUserByEmail(user.email)
        if (existing != null) {
            updateUser(user.copy(id = existing.id))
        } else {
            insertUser(user)
        }
    }
}
