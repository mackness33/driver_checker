package com.example.driverchecker.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.driverchecker.database.entity.EvaluationEntity


@Entity(
    tableName = "metrics_per_evaluation",
    foreignKeys = [
        ForeignKey(
            entity = EvaluationEntity::class,
            parentColumns = ["id"],
            childColumns = ["evaluation_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class MetricsPerEvaluationEntity (
    @ColumnInfo(name = "group") val group: String,
    @ColumnInfo(name = "tot_images") val totImages: Int,
    @ColumnInfo(name = "tot_classes") val totClasses: Int,
    @ColumnInfo(name = "tot_objects") val totObjects: Int,
    @ColumnInfo(name = "evaluation_id") val evaluationId: Long,
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
) {
    constructor(metrics: Pair<String, Triple<Int, Int, Int>?>, evalId: Long) : this (
        group = metrics.first,
        totImages = metrics.second?.first ?: 0,
        totClasses = metrics.second?.second ?: 0,
        totObjects = metrics.second?.third ?: 0,
        evaluationId = evalId,
    )
}
