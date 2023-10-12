package com.example.driverchecker.machinelearning.data

import androidx.room.ColumnInfo


/* NEW METRICS */
interface IAdditionalMetrics



interface WithWindowInfo {
    val data: List<IWindowBasicData>
}


interface WithGroupData<S> {
    val additionalMetrics: List<IGroupMetrics<S>>
}



interface IWindowBasicData : IWindowMetrics, IWindowSettingsOld

data class WindowBasicData (
    @ColumnInfo(name = "total_time") override val totalTime: Double,
    @ColumnInfo(name = "total_windows") override val totalWindows: Int,
    @ColumnInfo(name = "average_time") override val averageTime: Double,
    @ColumnInfo(name = "average_confidence") override val averageConfidence: Float,
    override val confidence: Float,
    @ColumnInfo(name = "group") override val supergroup: String,
    @ColumnInfo(name = "window_frames") override val windowFrames: Int,
    @ColumnInfo(name = "window_threshold") override val windowThreshold: Float,
    @ColumnInfo(name = "type") override val type: String
) : IWindowBasicData {
    constructor(copy: IWindowBasicData) : this (
        copy.totalTime,
        copy.totalWindows,
        copy.averageTime,
        copy.averageConfidence,
        copy.confidence,
        copy.supergroup,
        copy.windowFrames,
        copy.windowThreshold,
        copy.type
    )

    constructor(copyMetrics: IWindowMetrics, copySettings: IWindowSettingsOld) : this (
        copyMetrics.totalTime,
        copyMetrics.totalWindows,
        copyMetrics.averageTime,
        copyMetrics.averageConfidence,
        copyMetrics.confidence,
        copyMetrics.supergroup,
        copySettings.windowFrames,
        copySettings.windowThreshold,
        copySettings.type
    )
}


interface IWindowMetrics {
    val totalTime: Double
    val totalWindows: Int
    val averageTime: Double
    val averageConfidence: Float
    val confidence: Float
    val supergroup: String
}

data class WindowMetrics (
    @ColumnInfo(name = "total_time") override val totalTime: Double,
    @ColumnInfo(name = "total_windows") override val totalWindows: Int,
    @ColumnInfo(name = "average_time") override val averageTime: Double,
    @ColumnInfo(name = "average_confidence") override val averageConfidence: Float,
    override val confidence: Float,
    @ColumnInfo(name = "group") override val supergroup: String
) : IWindowMetrics {
    constructor(copy: IWindowMetrics?) : this (
        copy?.totalTime ?: 0.0,
        copy?.totalWindows ?: 0,
        copy?.averageTime ?: 0.0,
        copy?.averageConfidence ?: 0.0f,
        copy?.confidence ?: 0.0f,
        copy?.supergroup ?: ""
    )
}

/* OLD METRICS */
interface WithOldMetrics {
    val metrics: IOldMetrics?
}

interface IOldMetrics {
    val totalTime: Double
    val totalWindows: Int
}

interface IWindowOldMetrics : IOldMetrics {
    val type: String
}


data class WindowOldMetrics (
    override val totalTime: Double, override val totalWindows: Int, override val type: String
) : IWindowOldMetrics {
    constructor(copy: IWindowOldMetrics) : this(
        copy.totalTime, copy.totalWindows, copy.type
    )

    constructor() : this(0.0, 0,"")
}

data class MachineLearningOldMetrics (
    @ColumnInfo(name = "total_time") override val totalTime: Double,
    @ColumnInfo(name = "total_windows") override val totalWindows: Int
) : IOldMetrics {
    constructor(copy: IOldMetrics?) : this (
        copy?.totalTime ?: 0.0,
        copy?.totalWindows ?: 0,
    )
}



/* GROUP */

interface IGroupMetrics<S> : IAdditionalMetrics {
    val groupMetrics : Map<S, Triple<Int, Int, Int>>
}

interface IMutableGroupMetrics<in E : IClassificationOutput<S>, S> : IGroupMetrics<S> {
    fun initialize (keys: Set<S>)
    fun replace (element: E)
    fun add (element: E)
    fun subtract (element: E)
    fun remove (keys: Set<S>)
    fun clear ()

    fun copy() : Map<S, Triple<Int, Int, Int>>
    fun asImmutable() : IGroupMetrics<S>
}

data class GroupMetrics<S> (
    override val groupMetrics: Map<S, Triple<Int, Int, Int>>
) : IGroupMetrics<S> {
    constructor (listCopyEntity: List<Pair<S, Triple<Int, Int, Int>>>) : this (
        listCopyEntity.toMap().toMutableMap()
    )

//    constructor (listEntity: List<GroupMetricsEntity>) : this (
//        listCopyEntity.toMap().toMutableMap()
//    )

    constructor () : this (emptyMap())
}

/**
 * ICFR {
 *     metrics: Map<IWindowMetrics, IGroupMetrics?>
 *  }
 **/