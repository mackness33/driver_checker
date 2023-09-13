package com.example.driverchecker.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.driverchecker.machinelearning.data.IClassificationFinalResult
import com.example.driverchecker.machinelearning.data.IImageDetectionFinalResult
import com.example.driverchecker.machinelearning.data.ImageDetectionFinalResult
import com.example.driverchecker.machinelearning.data.WithConfAndGroups


@Entity(tableName = "evaluation")
data class EvaluationEntity (
    @ColumnInfo(name = "confidence") val confidence: Float,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "group") val group: String,
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
) {
    constructor(finalResult: IImageDetectionFinalResult<String>, title: String) : this (
        confidence = finalResult.confidence,
        name = title,
        group = finalResult.supergroup
    )
}
