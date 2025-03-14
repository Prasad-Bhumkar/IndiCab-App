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
        // TODO: Handle error scenarios where the driver might not be found; consider returning a default value or throwing a descriptive exception.
        Log.d("DriverService", "Fetching driver for userId=$userId")
        return driverDao.getDriverByUserId(userId)
    }

    suspend fun addDriver(driver: Driver) {
        // TODO: Validate driver data (e.g., required fields, correct formats) before adding to the database.
        Log.d("DriverService", "Adding driver: $driver")
        driverDao.insertDriver(driver)
    }

    suspend fun updateDriver(driver: Driver) {
        // TODO: Check for existence of driver record before updating; handle non-existent driver case gracefully.
        Log.d("DriverService", "Updating driver: $driver")
        driverDao.updateDriver(driver)
    }

    suspend fun removeDriver(driver: Driver) {
        // TODO: Handle cases when the driver does not exist (e.g., log a warning and skip removal).
        Log.d("DriverService", "Removing driver: $driver")
        driverDao.deleteDriver(driver)
    }

    fun getVehiclesByDriverId(driverId: String): Flow<List<Vehicle>> {
        // TODO: Add error handling for cases where no vehicles are found for the driver; consider returning an empty list or logging a warning.
        Log.d("DriverService", "Fetching vehicles for driverId=$driverId")
        return driverDao.getVehiclesByDriverId(driverId)
    }

    suspend fun addVehicle(vehicle: Vehicle) {
        // TODO: Validate vehicle data (e.g., required fields, correct formats) before adding to the database.
        Log.d("DriverService", "Adding vehicle: $vehicle")
        driverDao.insertVehicle(vehicle)
    }

    suspend fun updateVehicle(vehicle: Vehicle) {
        // TODO: Check for existence of vehicle record before updating; handle non-existent vehicle case gracefully.
        Log.d("DriverService", "Updating vehicle: $vehicle")
        driverDao.updateVehicle(vehicle)
    }

    suspend fun removeVehicle(vehicle: Vehicle) {
        // TODO: Handle cases when the vehicle does not exist (e.g., log a warning and skip removal).
        Log.d("DriverService", "Removing vehicle: $vehicle")
        driverDao.deleteVehicle(vehicle)
    }

    fun getDocumentsByDriverId(driverId: String): Flow<List<DriverDocument>> {
        // TODO: Add error handling for cases where no documents are found for the driver; consider returning an empty list or logging a warning.
        Log.d("DriverService", "Fetching documents for driverId=$driverId")
        return driverDao.getDocumentsByDriverId(driverId)
    }

    suspend fun addDocument(document: DriverDocument) {
        // TODO: Validate document data (e.g., required fields, correct formats) before adding to the database.
        Log.d("DriverService", "Adding document: $document")
        driverDao.insertDocument(document)
    }

    suspend fun updateDocument(document: DriverDocument) {
        // TODO: Check for existence of document record before updating; handle non-existent document case gracefully.
        Log.d("DriverService", "Updating document: $document")
        driverDao.updateDocument(document)
    }

    suspend fun removeDocument(document: DriverDocument) {
        // TODO: Handle cases when the document does not exist (e.g., log a warning and skip removal).
        Log.d("DriverService", "Removing document: $document")
        driverDao.deleteDocument(document)
    }
}
