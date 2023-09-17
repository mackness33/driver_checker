package com.example.driverchecker.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.driverchecker.machinelearning.data.IWindowOldMetrics

@Entity(
    tableName = "window_metrics",
    foreignKeys = [
        ForeignKey(
            entity = EvaluationEntity::class,
            parentColumns = ["id"],
            childColumns = ["evaluation_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
class WindowOldMetricsEntity (
    override val type: String,
    override val totalTime: Double,
    override val totalWindows: Int,
    @ColumnInfo(name = "evaluation_id") val evaluationId: Long,
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
) : IWindowOldMetrics {
    constructor(copy: IWindowOldMetrics, evaluationId: Long) : this (
        copy.type, copy.totalTime, copy.totalWindows, evaluationId
    )
}