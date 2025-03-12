package com.example.indicab.data.dao

import androidx.room.*
import com.example.indicab.models.*
import kotlinx.coroutines.flow.Flow

@Dao
interface DriverDao {
    @Query("SELECT * FROM drivers WHERE userId = :userId")
    fun getDriverByUserId(userId: String): Flow<Driver?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDriver(driver: Driver)

    @Update
    suspend fun updateDriver(driver: Driver)

    @Delete
    suspend fun deleteDriver(driver: Driver)

    @Query("SELECT * FROM vehicles WHERE driverId = :driverId")
    fun getVehiclesByDriverId(driverId: String): Flow<List<Vehicle>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVehicle(vehicle: Vehicle)

    @Update
    suspend fun updateVehicle(vehicle: Vehicle)

    @Delete
    suspend fun deleteVehicle(vehicle: Vehicle)

    @Query("SELECT * FROM driver_documents WHERE driverId = :driverId")
    fun getDocumentsByDriverId(driverId: String): Flow<List<DriverDocument>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocument(document: DriverDocument)

    @Update
    suspend fun updateDocument(document: DriverDocument)

    @Delete
    suspend fun deleteDocument(document: DriverDocument)
}
