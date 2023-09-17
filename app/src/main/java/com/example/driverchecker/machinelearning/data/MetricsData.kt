package com.example.driverchecker.machinelearning.data

import androidx.room.ColumnInfo


/* METRICS */
interface WithMetrics {
    val metrics: IMetrics?
}

interface IMetrics {
    val totalTime: Double
    val totalWindows: Int
}

interface IWindowMetrics : IMetrics {
    val type: String
}

interface IAdditionalMetrics

interface WithWindowMetrics {
    val metrics: Map<IWindowMetrics, IAdditionalMetrics?>
}

data class WindowMetrics (
    override val totalTime: Double, override val totalWindows: Int, override val type: String
) : IWindowMetrics {
    constructor(copy: IWindowMetrics) : this(
        copy.totalTime, copy.totalWindows, copy.type
    )

    constructor() : this(0.0, 0,"")
}

data class MachineLearningMetrics (
    @ColumnInfo(name = "total_time") override val totalTime: Double,
    @ColumnInfo(name = "total_windows") override val totalWindows: Int
) : IMetrics {
    constructor(copy: IMetrics?) : this (
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