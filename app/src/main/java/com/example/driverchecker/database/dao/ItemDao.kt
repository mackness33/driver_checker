package com.example.driverchecker.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.driverchecker.database.entity.ItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDao {

    @Query("SELECT * FROM item")
    fun getItems(): Flow<List<ItemEntity>>

    @Query("SELECT * FROM item WHERE partial_id = :partialId")
    fun getItemsPerPartial (partialId: Long): List<ItemEntity>


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(word: ItemEntity) : Long

    @Query("DELETE FROM item")
    suspend fun deleteAll()
}
