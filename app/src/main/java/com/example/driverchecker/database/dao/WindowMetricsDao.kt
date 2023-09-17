package com.example.driverchecker.database.dao

import androidx.room.*
import com.example.driverchecker.database.entity.WindowOldMetricsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WindowMetricsDao {

    @Query("SELECT * FROM window_metrics")
    fun getAllWindowMetrics(): Flow<List<WindowOldMetricsEntity>>

    @Query("SELECT * FROM window_metrics WHERE id = :windowMetricsId")
    fun getWindowMetrics(windowMetricsId: Long): WindowOldMetricsEntity

    @Query("SELECT * FROM window_metrics WHERE evaluation_id = :evaluationId")
    fun getWindowMetricsByEvaluation(evaluationId: Long): List<WindowOldMetricsEntity>


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(windowMetrics: WindowOldMetricsEntity) : Long

    @Delete
    suspend fun delete(windowMetrics: WindowOldMetricsEntity)

    @Query("DELETE FROM window_metrics")
    suspend fun deleteAll()

    @Query("DELETE FROM window_metrics WHERE id = :windowMetricsId")
    suspend fun deleteById(windowMetricsId: Long)
}
