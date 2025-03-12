package com.example.indicab.data.repositories

import com.example.indicab.data.dao.UserDao
import com.example.indicab.models.User
import com.example.indicab.models.UserProfile
import com.example.indicab.models.UserSettings
import kotlinx.coroutines.flow.Flow

class UserRepository(private val userDao: UserDao) {
    fun getUser(userId: String): Flow<User?> = userDao.getUser(userId)
    
    suspend fun getUserByEmail(email: String): User? = userDao.getUserByEmail(email)
    
    suspend fun getUserByPhone(phoneNumber: String): User? = userDao.getUserByPhone(phoneNumber)
    
    suspend fun createOrUpdateUser(user: User) = userDao.createOrUpdateUser(user)
    
    suspend fun updateUserProfile(userId: String, profile: UserProfile) =
        userDao.updateUserProfile(userId, profile)
    
    suspend fun updateUserSettings(userId: String, settings: UserSettings) =
        userDao.updateUserSettings(userId, settings)
    
    suspend fun markUserAsVerified(userId: String) = userDao.markUserAsVerified(userId)
    
    suspend fun updateLastLogin(userId: String) = userDao.updateLastLogin(userId)
    
    fun searchUsers(query: String): Flow<List<User>> = userDao.searchUsers(query)

    fun getUserProfile(userId: String) = userDao.getUserProfile(userId)
    
    fun getUserSettings(userId: String) = userDao.getUserSettings(userId)
    
    suspend fun deleteUser(user: User) = userDao.deleteUser(user)
}
