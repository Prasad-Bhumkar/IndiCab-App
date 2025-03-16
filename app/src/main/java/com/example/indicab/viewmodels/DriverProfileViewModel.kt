 package com.example.indicab.viewmodels
 
 import android.net.Uri
 import androidx.lifecycle.LiveData
 import androidx.lifecycle.MutableLiveData
 import androidx.lifecycle.ViewModel
 import androidx.lifecycle.viewModelScope
 import com.example.indicab.models.DriverProfile
 import kotlinx.coroutines.launch
 
 class DriverProfileViewModel : ViewModel() {
     
     private val _isLoading = MutableLiveData<Boolean>()
     val isLoading: LiveData<Boolean> = _isLoading
     
     private val _errorMessage = MutableLiveData<String?>()
     val errorMessage: LiveData<String?> = _errorMessage
     
     private val _driverProfile = MutableLiveData<DriverProfile>()
     val driverProfile: LiveData<DriverProfile> = _driverProfile
     
     private val _profilePhoto = MutableLiveData<Uri?>()
     val profilePhoto: LiveData<Uri?> = _profilePhoto
     
     private val _licensePhoto = MutableLiveData<Uri?>()
     val licensePhoto: LiveData<Uri?> = _licensePhoto
     
     private val _vehiclePhoto = MutableLiveData<Uri?>()
     val vehiclePhoto: LiveData<Uri?> = _vehiclePhoto
     
     private val _insurancePhoto = MutableLiveData<Uri?>()
     val insurancePhoto: LiveData<Uri?> = _insurancePhoto
     
     private val _isSaved = MutableLiveData<Boolean>()
     val isSaved: LiveData<Boolean> = _isSaved
     
     init {
         loadDriverProfile()
     }
     
     private fun loadDriverProfile() {
         _isLoading.value = true
         _errorMessage.value = null
         
         viewModelScope.launch {
             try {
                 // In a real app, you would load the driver profile from a repository
                 // For now, we'll create a dummy profile
                 val profile = DriverProfile(
                     id = "1",
                     name = "John Doe",
                     phone = "1234567890",
                     email = "john.doe@example.com",
                     licenseNumber = "DL12345678",
                     vehicleNumber = "KA01AB1234",
                     vehicleModel = "Honda City",
                     vehicleColor = "White",
                     rating = 4.5,
                     isVerified = true
                 )
                 
                 _driverProfile.value = profile
             } catch (e: Exception) {
                 _errorMessage.value = "An error occurred while loading driver profile: ${e.message}"
             } finally {
                 _isLoading.value = false
             }
         }
     }
     
     fun updateProfilePhoto(uri: Uri?) {
         _profilePhoto.value = uri
     }
     
     fun updateLicensePhoto(uri: Uri?) {
         _licensePhoto.value = uri
     }
     
     fun updateVehiclePhoto(uri: Uri?) {
         _vehiclePhoto.value = uri
     }
     
     fun updateInsurancePhoto(uri: Uri?) {
         _insurancePhoto.value = uri
     }
     
     fun saveDriverProfile(profile: DriverProfile) {
         _isLoading.value = true
         _errorMessage.value = null
         
         viewModelScope.launch {
             try {
                 // In a real app, you would save the driver profile to a repository
                 // For now, we'll just update the local state
                 _driverProfile.value = profile
                 _isSaved.value = true
             } catch (e: Exception) {
                 _errorMessage.value = "An error occurred while saving driver profile: ${e.message}"
             } finally {
                 _isLoading.value = false
             }
         }
     }
 } 