package com.example.driverchecker.database.dao

import androidx.room.*
import com.example.driverchecker.database.entity.MetricsPerEvaluationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MetricsPerEvaluationDao {

    @Query("SELECT * FROM metrics_per_evaluation")
    fun getAllMetrics(): Flow<List<MetricsPerEvaluationEntity>>

    @Query("SELECT * FROM metrics_per_evaluation WHERE id = :metricsPerEvaluationId")
    fun getMetrics(metricsPerEvaluationId: Long): MetricsPerEvaluationEntity

    @Query("SELECT * FROM metrics_per_evaluation WHERE evaluation_id = :evaluationId")
    fun getMetricsPerEvaluation(evaluationId: Long): List<MetricsPerEvaluationEntity>


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(metricsPerEvaluationId: MetricsPerEvaluationEntity) : Long

    @Delete
    suspend fun delete(metricsPerEvaluationId: MetricsPerEvaluationEntity)

    @Query("DELETE FROM metrics_per_evaluation")
    suspend fun deleteAll()

    @Query("DELETE FROM metrics_per_evaluation WHERE id = :metricsPerEvaluationId")
    suspend fun deleteById(metricsPerEvaluationId: Long)
}
