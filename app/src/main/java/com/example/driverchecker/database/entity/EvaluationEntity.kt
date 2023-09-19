package com.example.driverchecker.database.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.driverchecker.machinelearning.data.*


@Entity(tableName = "evaluation")
data class EvaluationEntity (
    override val confidence: Float,
    val name: String,
    @ColumnInfo(name = "group") override val supergroup: String,
    @Embedded override val settings: OldSettings?,
    @Embedded override val metrics: MachineLearningOldMetrics?,
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
) : IOldImageDetectionFinalResult<String> {
    constructor(finalResult: IOldImageDetectionFinalResult<String>, title: String) : this (
        confidence = finalResult.confidence,
        name = title,
        supergroup = finalResult.supergroup,
        settings = OldSettings(finalResult.settings),
        metrics = MachineLearningOldMetrics(finalResult.metrics)
    )
}
