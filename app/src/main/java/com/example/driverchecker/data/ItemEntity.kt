package com.example.driverchecker.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey


@Entity(
    tableName = "item",
    foreignKeys = [
        ForeignKey(
            entity = PartialEntity::class,
            parentColumns = ["partial_id"],
            childColumns = ["id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ItemEntity (
    @ColumnInfo(name = "confidence") val confidence: Float,
    @ColumnInfo(name = "classification") val classification: String,
    @ColumnInfo(name = "partial_id") val partialId: String,
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
)