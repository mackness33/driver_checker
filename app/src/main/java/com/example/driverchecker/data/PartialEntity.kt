package com.example.driverchecker.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey


@Entity(
    tableName = "partial",
    foreignKeys = [
        ForeignKey(
            entity = EvaluationEntity::class,
            parentColumns = ["evaluation_id"],
            childColumns = ["id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class PartialEntity (
    @ColumnInfo(name = "confidence") val confidence: Float,
    @ColumnInfo(name = "index") val index: Int,
    @ColumnInfo(name = "evaluation_id") val evaluationId: Int,
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
)