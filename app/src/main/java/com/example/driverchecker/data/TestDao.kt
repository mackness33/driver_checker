package com.example.driverchecker.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TestDao {

    @Query("SELECT * FROM test_table ORDER BY test ASC")
    fun getAlphabetizedWords(): Flow<List<TestEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(word: TestEntity)

    @Query("DELETE FROM test_table")
    suspend fun deleteAll()
}
