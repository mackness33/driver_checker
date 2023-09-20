package com.example.driverchecker.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "group_metrics",
    foreignKeys = [
        ForeignKey(
            entity = WindowInformationEntity::class,
            parentColumns = ["id"],
            childColumns = ["window_information_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
class GroupMetricsEntity (
    val group: String,
    @ColumnInfo(name = "total_images") val totalImages: Int,
    @ColumnInfo(name = "total_classes") val totalClasses: Int,
    @ColumnInfo(name = "total_objects") val totalObjects: Int,
    @ColumnInfo(name = "window_information_id") val windowMetricsId: Long,
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
) {
    constructor(copy: Pair<String, Triple<Int, Int, Int>>, windowMetricsId: Long) : this (
        copy.first,
        copy.second.first, copy.second.second, copy.second.third,
        windowMetricsId
    )

    constructor(group: String, metrics: Triple<Int, Int, Int>, windowMetricsId: Long) : this (
        group,
        metrics.first, metrics.second, metrics.third,
        windowMetricsId
    )
}