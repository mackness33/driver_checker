package com.example.driverchecker.database.entity

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
    val confidence: Float,
    val index: Int,
    val group: String,
    val path: String?,
    @ColumnInfo(name = "evaluation_id") val evaluationId: Long,
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
) {
    constructor(partialResult: IImageDetectionOutput<String>, outputIndex: Int, evalId: Long, imagePath: String?) : this (
        confidence = partialResult.stats.confidence,
        index = outputIndex,
        group = partialResult.stats.groups.keys.first(),
        path = imagePath,
        evaluationId = evalId
    )
}