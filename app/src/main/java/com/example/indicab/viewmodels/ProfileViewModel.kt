package com.example.indicab.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File

class ProfileViewModel : ViewModel() {
    private val _profileState = MutableStateFlow<ProfileState>(ProfileState.Loading)
    val profileState: StateFlow<ProfileState> = _profileState

    private val _editState = MutableStateFlow<EditProfileState>(EditProfileState.Initial)
    val editState: StateFlow<EditProfileState> = _editState

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            _profileState.value = ProfileState.Loading
            try {
                // In real implementation, this would fetch from a repository/API
                val profile = UserProfile(
                    id = "user123",
                    name = "John Doe",
                    email = "john.doe@example.com",
                    phone = "+91 9876543210",
                    profilePicture = null,
                    rating = 4.5f,
                    totalRides = 25
                )
                _profileState.value = ProfileState.Success(profile)
            } catch (e: Exception) {
                _profileState.value = ProfileState.Error(e.message ?: "Failed to load profile")
            }
        }
    }

    fun updateProfile(
        name: String? = null,
        email: String? = null,
        phone: String? = null,
        profilePicture: File? = null
    ) {
        viewModelScope.launch {
            _editState.value = EditProfileState.Updating
            try {
                // In real implementation, this would call an API
                // Simulate network delay
                kotlinx.coroutines.delay(1000)
                
                val currentProfile = (profileState.value as? ProfileState.Success)?.profile
                    ?: throw IllegalStateException("Profile not loaded")

                val updatedProfile = currentProfile.copy(
                    name = name ?: currentProfile.name,
                    email = email ?: currentProfile.email,
                    phone = phone ?: currentProfile.phone
                )

                _profileState.value = ProfileState.Success(updatedProfile)
                _editState.value = EditProfileState.Success
            } catch (e: Exception) {
                _editState.value = EditProfileState.Error(e.message ?: "Failed to update profile")
            }
        }
    }
}

sealed class ProfileState {
    object Loading : ProfileState()
    data class Success(val profile: UserProfile) : ProfileState()
    data class Error(val message: String) : ProfileState()
}

sealed class EditProfileState {
    object Initial : EditProfileState()
    object Updating : EditProfileState()
    object Success : EditProfileState()
    data class Error(val message: String) : EditProfileState()
}

data class UserProfile(
    val id: String,
    val name: String,
    val email: String,
    val phone: String,
    val profilePicture: String?,
    val rating: Float,
    val totalRides: Int
)
