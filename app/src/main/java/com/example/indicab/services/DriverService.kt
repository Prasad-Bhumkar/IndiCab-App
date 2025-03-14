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
        Log.d("DriverService", "Fetching driver for userId=$userId")
        return driverDao.getDriverByUserId(userId)
    }

    suspend fun addDriver(driver: Driver) {
        Log.d("DriverService", "Adding driver: $driver")
        driverDao.insertDriver(driver)
    }

    suspend fun updateDriver(driver: Driver) {
        Log.d("DriverService", "Updating driver: $driver")
        driverDao.updateDriver(driver)
    }

    suspend fun removeDriver(driver: Driver) {
        Log.d("DriverService", "Removing driver: $driver")
        driverDao.deleteDriver(driver)
    }

    fun getVehiclesByDriverId(driverId: String): Flow<List<Vehicle>> {
        Log.d("DriverService", "Fetching vehicles for driverId=$driverId")
        return driverDao.getVehiclesByDriverId(driverId)
    }

    suspend fun addVehicle(vehicle: Vehicle) {
        Log.d("DriverService", "Adding vehicle: $vehicle")
        driverDao.insertVehicle(vehicle)
    }

    suspend fun updateVehicle(vehicle: Vehicle) {
        Log.d("DriverService", "Updating vehicle: $vehicle")
        driverDao.updateVehicle(vehicle)
    }

    suspend fun removeVehicle(vehicle: Vehicle) {
        Log.d("DriverService", "Removing vehicle: $vehicle")
        driverDao.deleteVehicle(vehicle)
    }

    fun getDocumentsByDriverId(driverId: String): Flow<List<DriverDocument>> {
        Log.d("DriverService", "Fetching documents for driverId=$driverId")
        return driverDao.getDocumentsByDriverId(driverId)
    }

    suspend fun addDocument(document: DriverDocument) {
        Log.d("DriverService", "Adding document: $document")
        driverDao.insertDocument(document)
    }

    suspend fun updateDocument(document: DriverDocument) {
        Log.d("DriverService", "Updating document: $document")
        driverDao.updateDocument(document)
    }

    suspend fun removeDocument(document: DriverDocument) {
        Log.d("DriverService", "Removing document: $document")
        driverDao.deleteDocument(document)
    }
}
