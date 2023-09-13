package com.example.driverchecker.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.driverchecker.machinelearning.data.IImageDetectionOutput


@Entity(
    tableName = "partial",
    foreignKeys = [
        ForeignKey(
            entity = EvaluationEntity::class,
            parentColumns = ["id"],
            childColumns = ["evaluation_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class PartialEntity (
    @ColumnInfo(name = "confidence") val confidence: Float,
    @ColumnInfo(name = "index") val index: Int,
    @ColumnInfo(name = "group") val group: String,
    @ColumnInfo(name = "evaluation_id") val evaluationId: Int,
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
) {
    constructor(partialResult: IImageDetectionOutput<String>, outputIndex: Int, evalId: Int) : this (
        confidence = partialResult.confidence,
        index = outputIndex,
        group = partialResult.groups.keys.first(),
        evaluationId = evalId
    )
}