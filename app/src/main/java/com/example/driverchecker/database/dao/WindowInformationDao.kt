package com.example.driverchecker.database.dao

import androidx.room.*
import com.example.driverchecker.database.entity.WindowInformationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WindowInformationDao {

    @Query("SELECT * FROM window_information")
    fun getAllWindowInformation(): Flow<List<WindowInformationEntity>>

    @Query("SELECT * FROM window_information WHERE id = :windowInformationId")
    fun getWindowInformation(windowInformationId: Long): WindowInformationEntity

    @Query("SELECT * FROM window_information WHERE evaluation_id = :evaluationId")
    fun getWindowInformationByEvaluation(evaluationId: Long): List<WindowInformationEntity>


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(windowMetrics: WindowInformationEntity) : Long

    @Delete
    suspend fun delete(windowMetrics: WindowInformationEntity)

    @Query("DELETE FROM window_information")
    suspend fun deleteAll()

    @Query("DELETE FROM window_information WHERE id = :windowInformationId")
    suspend fun deleteById(windowInformationId: Long)
}
