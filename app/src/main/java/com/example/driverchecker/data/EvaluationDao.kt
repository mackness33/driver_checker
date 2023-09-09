package com.example.driverchecker.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface EvaluationDao {

    @Query("SELECT * FROM evaluation")
    fun getEvaluations(): Flow<List<EvaluationEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(word: EvaluationEntity)

    @Query("DELETE FROM evaluation")
    suspend fun deleteAll()
}
