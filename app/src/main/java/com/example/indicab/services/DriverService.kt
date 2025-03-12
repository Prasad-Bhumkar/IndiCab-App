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
