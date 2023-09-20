package com.example.driverchecker.database.entity

import androidx.room.*
import com.example.driverchecker.machinelearning.data.*


@Entity(
    tableName = "evaluation",
)
data class EvaluationEntity (
    override val confidence: Float,
    val name: String,
    @ColumnInfo(name = "group") override val supergroup: String,
//    @Embedded override val settings: OldSettings?,
//    @Embedded override val metrics: MachineLearningOldMetrics?,
//    override val data: Map<IWindowBasicData, IGroupMetrics<String>?>,
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
) : IClassificationFinalResultStats<String> {
    constructor(finalResult: IImageDetectionFinalResult<String>, title: String) : this (
        confidence = finalResult.confidence,
        name = title,
        supergroup = finalResult.supergroup,
//        data = finalResult.data
    )
}
