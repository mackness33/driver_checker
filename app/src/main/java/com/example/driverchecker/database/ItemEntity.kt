package com.example.driverchecker.database

import android.graphics.RectF
import androidx.room.*
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
    @ColumnInfo(name = "internal_index") val internalIndex: Int,
    @ColumnInfo(name = "partial_id") val partialId: Long,
    @Embedded val rect: RectangleF,
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
) {
    constructor(itemResult: IImageDetectionItem<String>, partId: Long) : this (
        confidence = itemResult.confidence,
        classification = itemResult.classification.supergroup,
        internalIndex = itemResult.classification.internalIndex,
        partialId = partId,
        rect = RectangleF(itemResult.rect)
    )
}

data class RectangleF(
    val left: Float,
    val top: Float,
    val right: Float,
    val bottom: Float,
) {
    constructor(rect: RectF) : this (
        left = rect.left,
        top = rect.top,
        right = rect.right,
        bottom = rect.bottom,
    )
}
