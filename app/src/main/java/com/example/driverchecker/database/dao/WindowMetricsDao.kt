package com.example.driverchecker.database.dao

import androidx.room.*
import com.example.driverchecker.database.entity.MetricsPerEvaluationEntity
import com.example.driverchecker.database.entity.WindowMetricsEntity
import com.example.driverchecker.machinelearning.data.WindowMetrics
import kotlinx.coroutines.flow.Flow

@Dao
interface WindowMetricsDao {

    @Query("SELECT * FROM window_metrics")
    fun getAllWindowMetrics(): Flow<List<WindowMetricsEntity>>

    @Query("SELECT * FROM window_metrics WHERE id = :windowMetricsId")
    fun getWindowMetrics(windowMetricsId: Long): WindowMetricsEntity

    @Query("SELECT * FROM window_metrics WHERE evaluation_id = :evaluationId")
    fun getWindowMetricsByEvaluation(evaluationId: Long): List<WindowMetricsEntity>


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(windowMetrics: WindowMetricsEntity) : Long

    @Delete
    suspend fun delete(windowMetrics: WindowMetricsEntity)

    @Query("DELETE FROM window_metrics")
    suspend fun deleteAll()

    @Query("DELETE FROM window_metrics WHERE id = :windowMetricsId")
    suspend fun deleteById(windowMetricsId: Long)
}
