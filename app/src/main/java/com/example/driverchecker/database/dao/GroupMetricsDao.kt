package com.example.driverchecker.database.dao

import androidx.room.*
import com.example.driverchecker.database.entity.GroupMetricsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GroupMetricsDao {

    @Query("SELECT * FROM group_metrics")
    fun getAllGroupMetrics(): Flow<List<GroupMetricsEntity>>


    @Query("SELECT * FROM group_metrics WHERE id = :groupMetricsId")
    fun getGroupMetrics(groupMetricsId: Long): GroupMetricsEntity

    @Query("SELECT * FROM group_metrics WHERE window_information_id = :windowInformationId")
    fun getGroupMetricsByWindowMetrics(windowInformationId: Long): List<GroupMetricsEntity>


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(windowMetrics: GroupMetricsEntity) : Long

    @Delete
    suspend fun delete(windowMetrics: GroupMetricsEntity)

    @Query("DELETE FROM group_metrics")
    suspend fun deleteAll()

    @Query("DELETE FROM group_metrics WHERE id = :groupMetricsId")
    suspend fun deleteById(groupMetricsId: Long)
}
