package com.example.driverchecker.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface EvaluationDao {

    @Query("SELECT * FROM evaluation")
    fun getEvaluations(): Flow<List<EvaluationEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(evaluation: EvaluationEntity)
    
    @Delete
    suspend fun delete(evaluation: EvaluationEntity)

    @Query("DELETE FROM evaluation")
    suspend fun deleteAll()

    @Query("DELETE FROM evaluation WHERE id = :evaluationId")
    suspend fun deleteById(evaluationId: Int)
}
