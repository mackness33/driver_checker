package com.example.driverchecker.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey


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
    @ColumnInfo(name = "evaluation_id") val evaluationId: Int,
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
)