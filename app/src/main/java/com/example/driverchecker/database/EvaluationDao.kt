package com.example.driverchecker.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface EvaluationDao {

    @Query("SELECT * FROM evaluation")
    fun getAllEvaluations(): Flow<List<EvaluationEntity>>

    @Query("SELECT * FROM evaluation WHERE id = :evaluationId")
    fun getEvaluation(evaluationId: Long): EvaluationEntity

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(evaluation: EvaluationEntity) : Long
    
    @Delete
    suspend fun delete(evaluation: EvaluationEntity)

    @Query("DELETE FROM evaluation")
    suspend fun deleteAll()

    @Query("DELETE FROM evaluation WHERE id = :evaluationId")
    suspend fun deleteById(evaluationId: Long)
}
