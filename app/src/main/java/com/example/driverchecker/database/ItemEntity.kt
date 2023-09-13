package com.example.driverchecker.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.driverchecker.machinelearning.data.IImageDetectionItem


@Entity(
    tableName = "item",
    foreignKeys = [
        ForeignKey(
            entity = PartialEntity::class,
            parentColumns = ["id"],
            childColumns = ["partial_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ItemEntity (
    @ColumnInfo(name = "confidence") val confidence: Float,
    @ColumnInfo(name = "classification") val classification: String,
    @ColumnInfo(name = "partial_id") val partialId: Int,
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
) {
    constructor(itemResult: IImageDetectionItem<String>, partId: Int) : this (
        confidence = itemResult.confidence,
        classification = itemResult.classification.supergroup,
        partialId = partId
    )
}
