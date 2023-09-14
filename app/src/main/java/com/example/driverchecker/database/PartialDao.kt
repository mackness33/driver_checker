package com.example.driverchecker.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PartialDao {

    @Query("SELECT * FROM partial")
    fun getAllPartials(): Flow<List<PartialEntity>>

    @Query("SELECT * FROM partial WHERE id = :partialId")
    fun getPartial(partialId: Long): PartialEntity

    @Query("SELECT * FROM partial WHERE evaluation_id = :evaluationId")
    fun getPartialsPerEvaluation(evaluationId: Long): Flow<List<PartialEntity>>


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(partial: PartialEntity) : Long

    @Delete
    suspend fun delete(partial: PartialEntity)

    @Query("DELETE FROM partial")
    suspend fun deleteAll()

    @Query("DELETE FROM partial WHERE id = :partialId")
    suspend fun deleteById(partialId: Long)
}
