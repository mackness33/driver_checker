package com.example.driverchecker.database.entity

import androidx.room.*
import com.example.driverchecker.machinelearning.data.*

@Entity(
    tableName = "window_information",
    foreignKeys = [
        ForeignKey(
            entity = EvaluationEntity::class,
            parentColumns = ["id"],
            childColumns = ["evaluation_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
class WindowInformationEntity (
    @Embedded val metrics: WindowMetrics,
    @Embedded val settings: WindowSettings,
    @ColumnInfo(name = "evaluation_id") val evaluationId: Long,
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
) {
    constructor(copyMetrics: IWindowMetrics, copySettings: IWindowSettings, evaluationId: Long) : this (
        WindowMetrics(copyMetrics), WindowSettings(copySettings), evaluationId
    )

    constructor(copyData: IWindowBasicData, evaluationId: Long) : this (
        WindowMetrics(copyData), WindowSettings(copyData), evaluationId
    )
}