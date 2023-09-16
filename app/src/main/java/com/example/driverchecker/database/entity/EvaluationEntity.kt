package com.example.driverchecker.database.entity

import android.text.BoringLayout
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.driverchecker.machinelearning.data.IImageDetectionFinalResult
import com.example.driverchecker.machinelearning.data.IImageDetectionFullFinalResult
import com.example.driverchecker.machinelearning.data.IMetrics
import com.example.driverchecker.machinelearning.data.MachineLearningMetrics
import com.example.driverchecker.utils.ISettings
import com.example.driverchecker.utils.Settings


@Entity(tableName = "evaluation")
data class EvaluationEntity (
    override val confidence: Float,
    val name: String,
    @ColumnInfo(name = "group") override val supergroup: String,
    @Embedded override val settings: Settings?,
    @Embedded override val metrics: MachineLearningMetrics?,
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
) : IImageDetectionFinalResult<String> {
    constructor(finalResult: IImageDetectionFinalResult<String>, title: String) : this (
        confidence = finalResult.confidence,
        name = title,
        supergroup = finalResult.supergroup,
        settings = Settings(finalResult.settings),
        metrics = MachineLearningMetrics(finalResult.metrics)
    )
}
