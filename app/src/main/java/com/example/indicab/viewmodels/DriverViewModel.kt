 package com.example.indicab.viewmodels
 
 import androidx.lifecycle.ViewModel
 import androidx.lifecycle.ViewModelProvider
 import androidx.lifecycle.viewModelScope
 import com.example.indicab.models.*
 import com.example.indicab.services.DriverService
 import kotlinx.coroutines.flow.*
 import kotlinx.coroutines.launch
 import javax.inject.Inject
 
 class DriverViewModel @Inject constructor(
     private val driverService: DriverService,
     private val userId: String
 ) : ViewModel() {
 
     private val _driverState = MutableStateFlow<DriverState>(DriverState.Loading)
     val driverState = _driverState.asStateFlow()
 
     val driver = driverService.getDriverByUserId(userId)
         .stateIn(
             scope = viewModelScope,
             started = SharingStarted.WhileSubscribed(5000),
             initialValue = null
         )
 
     init {
         loadDriver()
     }
 
     private fun loadDriver() {
         viewModelScope.launch {
             driver.collect { driver ->
                 _driverState.value = if (driver != null) {
                     DriverState.Success(driver)
                 } else {
                     DriverState.Error("Driver not found")
                 }
             }
         }
     }
 
     fun addDriver(driver: Driver) {
         viewModelScope.launch {
             try {
                 _driverState.value = DriverState.Processing
                 driverService.addDriver(driver)
                 _driverState.value = DriverState.DriverAdded(driver)
             } catch (e: Exception) {
                 _driverState.value = DriverState.Error(e.message ?: "Failed to add driver")
             }
         }
     }
 
     fun updateDriver(driver: Driver) {
         viewModelScope.launch {
             try {
                 _driverState.value = DriverState.Processing
                 driverService.updateDriver(driver)
                 _driverState.value = DriverState.DriverUpdated(driver)
             } catch (e: Exception) {
                 _driverState.value = DriverState.Error(e.message ?: "Failed to update driver")
             }
         }
     }
 
     fun removeDriver(driver: Driver) {
         viewModelScope.launch {
             try {
                 _driverState.value = DriverState.Processing
                 driverService.removeDriver(driver)
                 _driverState.value = DriverState.DriverRemoved(driver)
             } catch (e: Exception) {
                 _driverState.value = DriverState.Error(e.message ?: "Failed to remove driver")
             }
         }
     }
 
     class Factory @Inject constructor(
         private val driverService: DriverService,
         private val userId: String
     ) : ViewModelProvider.Factory {
         @Suppress("UNCHECKED_CAST")
         override fun <T : ViewModel> create(modelClass: Class<T>): T {
             if (modelClass.isAssignableFrom(DriverViewModel::class.java)) {
                 return DriverViewModel(driverService, userId) as T
             }
             throw IllegalArgumentException("Unknown ViewModel class")
         }
     }
 }
 
 sealed class DriverState {
     object Loading : DriverState()
     data class Success(val driver: Driver) : DriverState()
     data class DriverAdded(val driver: Driver) : DriverState()
     data class DriverUpdated(val driver: Driver) : DriverState()
     data class DriverRemoved(val driver: Driver) : DriverState()
     data class Error(val message: String) : DriverState()
 }
