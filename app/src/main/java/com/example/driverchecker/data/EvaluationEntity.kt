package com.example.driverchecker.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "evaluation")
data class EvaluationEntity (
    @ColumnInfo(name = "confidence") val confidence: Float,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "group") val group: Float,
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
)
