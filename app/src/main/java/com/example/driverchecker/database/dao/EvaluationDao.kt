package com.example.driverchecker.database.dao

import androidx.room.*
import com.example.driverchecker.database.entity.EvaluationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EvaluationDao {

    @Query("SELECT * FROM evaluation")
    fun getAllEvaluations(): Flow<List<EvaluationEntity>>

    @Query("SELECT * FROM evaluation WHERE id = :evaluationId")
    fun getEvaluation(evaluationId: Long): EvaluationEntity

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(evaluation: EvaluationEntity) : Long

    @Query("UPDATE evaluation SET name = :name WHERE id = :evaluationId")
    suspend fun updateById(evaluationId: Long, name: String)
    
    @Delete
    suspend fun delete(evaluation: EvaluationEntity)

    @Query("DELETE FROM evaluation")
    suspend fun deleteAll()

    @Query("DELETE FROM evaluation WHERE id = :evaluationId")
    suspend fun deleteById(evaluationId: Long)
}
