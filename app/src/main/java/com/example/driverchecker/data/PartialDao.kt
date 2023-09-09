package com.example.driverchecker.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PartialDao {

    @Query("SELECT * FROM partial")
    fun getPartials(): Flow<List<PartialEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(word: PartialEntity)

    @Query("DELETE FROM partial")
    suspend fun deleteAll()
}
