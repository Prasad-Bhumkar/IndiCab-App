<<<<<<< HEAD
 package com.example.indicab.services
 
 import com.example.indicab.data.dao.DriverDao
 import com.example.indicab.models.*
 import kotlinx.coroutines.flow.Flow
 import javax.inject.Inject
 import javax.inject.Singleton
 
 @Singleton
 class DriverService @Inject constructor(
     private val driverDao: DriverDao
 ) {
     fun getDriverByUserId(userId: String): Flow<Driver?> {
         return driverDao.getDriverByUserId(userId)
     }
 
     suspend fun addDriver(driver: Driver) {
         driverDao.insertDriver(driver)
     }
 
     suspend fun updateDriver(driver: Driver) {
         driverDao.updateDriver(driver)
     }
 
     suspend fun removeDriver(driver: Driver) {
         driverDao.deleteDriver(driver)
     }
 
     fun getVehiclesByDriverId(driverId: String): Flow<List<Vehicle>> {
         return driverDao.getVehiclesByDriverId(driverId)
     }
 
     suspend fun addVehicle(vehicle: Vehicle) {
         driverDao.insertVehicle(vehicle)
     }
 
     suspend fun updateVehicle(vehicle: Vehicle) {
         driverDao.updateVehicle(vehicle)
     }
 
     suspend fun removeVehicle(vehicle: Vehicle) {
         driverDao.deleteVehicle(vehicle)
     }
 
     fun getDocumentsByDriverId(driverId: String): Flow<List<DriverDocument>> {
         return driverDao.getDocumentsByDriverId(driverId)
     }
 
     suspend fun addDocument(document: DriverDocument) {
         driverDao.insertDocument(document)
     }
 
     suspend fun updateDocument(document: DriverDocument) {
         driverDao.updateDocument(document)
     }
 
     suspend fun removeDocument(document: DriverDocument) {
         driverDao.deleteDocument(document)
     }
 }
=======
rpackage com.example.indicab.services

import com.example.indicab.data.dao.DriverDao
import com.example.indicab.models.*
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton
import android.util.Log

@Singleton
class DriverService @Inject constructor(
    private val driverDao: DriverDao
) {
    fun getDriverByUserId(userId: String): Flow<Driver?> {
        // Handle error scenarios where the driver might not be found
        // TODO: Handle error scenarios where the driver might not be found; consider returning a default value or throwing a descriptive exception.
        Log.d("DriverService", "Fetching driver for userId=$userId")
        return driverDao.getDriverByUserId(userId)
    }

    suspend fun addDriver(driver: Driver) {
        // Validate driver data (e.g., required fields, correct formats) before adding to the database
        // TODO: Validate driver data (e.g., required fields, correct formats) before adding to the database.
        Log.d("DriverService", "Adding driver: $driver")
        driverDao.insertDriver(driver)
    }

    suspend fun updateDriver(driver: Driver) {
        // Check for existence of driver record before updating
        // TODO: Check for existence of driver record before updating; handle non-existent driver case gracefully.
        Log.d("DriverService", "Updating driver: $driver")
        driverDao.updateDriver(driver)
    }

    suspend fun removeDriver(driver: Driver) {
        // Handle cases when the driver does not exist
        // TODO: Handle cases when the driver does not exist (e.g., log a warning and skip removal).
        Log.d("DriverService", "Removing driver: $driver")
        driverDao.deleteDriver(driver)
    }

    fun getVehiclesByDriverId(driverId: String): Flow<List<Vehicle>> {
        // Add error handling for cases where no vehicles are found for the driver
        // TODO: Add error handling for cases where no vehicles are found for the driver; consider returning an empty list or logging a warning.
        Log.d("DriverService", "Fetching vehicles for driverId=$driverId")
        return driverDao.getVehiclesByDriverId(driverId)
    }

    suspend fun addVehicle(vehicle: Vehicle) {
        // Validate vehicle data (e.g., required fields, correct formats) before adding to the database
        // TODO: Validate vehicle data (e.g., required fields, correct formats) before adding to the database.
        Log.d("DriverService", "Adding vehicle: $vehicle")
        driverDao.insertVehicle(vehicle)
    }

    suspend fun updateVehicle(vehicle: Vehicle) {
        // Check for existence of vehicle record before updating
        // TODO: Check for existence of vehicle record before updating; handle non-existent vehicle case gracefully.
        Log.d("DriverService", "Updating vehicle: $vehicle")
        driverDao.updateVehicle(vehicle)
    }

    suspend fun removeVehicle(vehicle: Vehicle) {
        // Handle cases when the vehicle does not exist
        // TODO: Handle cases when the vehicle does not exist (e.g., log a warning and skip removal).
        Log.d("DriverService", "Removing vehicle: $vehicle")
        driverDao.deleteVehicle(vehicle)
    }

    fun getDocumentsByDriverId(driverId: String): Flow<List<DriverDocument>> {
        // Add error handling for cases where no documents are found for the driver
        // TODO: Add error handling for cases where no documents are found for the driver; consider returning an empty list or logging a warning.
        Log.d("DriverService", "Fetching documents for driverId=$driverId")
        return driverDao.getDocumentsByDriverId(driverId)
    }

    suspend fun addDocument(document: DriverDocument) {
        // Validate document data (e.g., required fields, correct formats) before adding to the database
        // TODO: Validate document data (e.g., required fields, correct formats) before adding to the database.
        Log.d("DriverService", "Adding document: $document")
        driverDao.insertDocument(document)
    }

    suspend fun updateDocument(document: DriverDocument) {
        // Check for existence of document record before updating
        // TODO: Check for existence of document record before updating; handle non-existent document case gracefully.
        Log.d("DriverService", "Updating document: $document")
        driverDao.updateDocument(document)
    }

    suspend fun removeDocument(document: DriverDocument) {
        // Handle cases when the document does not exist
        // TODO: Handle cases when the document does not exist (e.g., log a warning and skip removal).
        Log.d("DriverService", "Removing document: $document")
        driverDao.deleteDocument(document)
    }
}
>>>>>>> 81ec31f166cdb0573d5c5135fcdecb0f6ba49d83
