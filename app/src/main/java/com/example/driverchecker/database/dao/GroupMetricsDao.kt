package com.example.driverchecker.database.dao

import androidx.room.*
import com.example.driverchecker.database.entity.GroupMetricsEntity
import com.example.driverchecker.database.entity.MetricsPerEvaluationEntity
import com.example.driverchecker.database.entity.WindowMetricsEntity
import com.example.driverchecker.machinelearning.data.WindowMetrics
import kotlinx.coroutines.flow.Flow

@Dao
interface GroupMetricsDao {

    @Query("SELECT * FROM groupMetrics")
    fun getAllGroupMetrics(): Flow<List<GroupMetricsEntity>>


    @Query("SELECT * FROM group_metrics WHERE id = :groupMetricsId")
    fun getGroupMetrics(groupMetricsId: Long): GroupMetricsEntity

    @Query("SELECT * FROM group_metrics WHERE windowMetricsId = :windowMetricsId")
    fun getGroupMetricsByWindowMetrics(windowMetricsId: Long): List<GroupMetricsEntity>


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(windowMetrics: GroupMetricsEntity) : Long

    @Delete
    suspend fun delete(windowMetrics: GroupMetricsEntity)

    @Query("DELETE FROM group_metrics")
    suspend fun deleteAll()

    @Query("DELETE FROM group_metrics WHERE id = :groupMetricsId")
    suspend fun deleteById(groupMetricsId: Long)
}
