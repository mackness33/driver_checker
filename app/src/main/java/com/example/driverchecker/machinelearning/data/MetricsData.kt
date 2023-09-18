package com.example.driverchecker.machinelearning.data

import androidx.room.ColumnInfo


/* NEW METRICS */
interface IAdditionalMetrics

interface WithWindowData {
    val data: Map<IWindowData, IAdditionalMetrics?>
}

interface WithGroupsData<S> : WithWindowData {
    override val data: Map<IWindowData, IGroupMetrics<S>?>
}

interface IWindowData : IWindowMetrics, IWindowSettings

data class WindowData (
    @ColumnInfo(name = "total_time") override val totalTime: Double,
    @ColumnInfo(name = "total_windows") override val totalWindows: Int,
    @ColumnInfo(name = "window_frames") override val windowFrames: Int,
    @ColumnInfo(name = "window_threshold") override val windowThreshold: Float,
    @ColumnInfo(name = "type") override val type: String
) : IWindowData {
    constructor(copy: IWindowData) : this (
        copy.totalTime,
        copy.totalWindows,
        copy.windowFrames,
        copy.windowThreshold,
        copy.type
    )

    constructor(copyMetrics: IWindowMetrics, copySettings: IWindowSettings) : this (
        copyMetrics.totalTime,
        copyMetrics.totalWindows,
        copySettings.windowFrames,
        copySettings.windowThreshold,
        copySettings.type
    )
}

interface WithMetrics {
    val metrics: IWindowMetrics?
}


interface IWindowMetrics {
    val totalTime: Double
    val totalWindows: Int
}

data class WindowMetrics (
    @ColumnInfo(name = "total_time") override val totalTime: Double,
    @ColumnInfo(name = "total_windows") override val totalWindows: Int
) : IWindowMetrics {
    constructor(copy: IWindowMetrics?) : this (
        copy?.totalTime ?: 0.0,
        copy?.totalWindows ?: 0,
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

interface IMutableGroupMetrics<S> : IGroupMetrics<S> {
    fun initialize (keys: Set<S>)
    fun replace (element: IClassificationOutputStats<S>)
    fun add (element: IClassificationOutputStats<S>)
    fun subtract (element: IClassificationOutputStats<S>)
    fun remove (keys: Set<S>)
    fun clear ()
}

data class GroupMetrics<S> (
    override val groupMetrics: Map<S, Triple<Int, Int, Int>>
) : IGroupMetrics<S> {
    constructor (listCopyEntity: List<Pair<S, Triple<Int, Int, Int>>>) : this (
        listCopyEntity.toMap()
    )

    constructor () : this (emptyMap())
}

/**
 * ICFR {
 *     metrics: Map<IWindowMetrics, IGroupMetrics?>
 *  }
 **/