package com.example.driverchecker.machinelearning.helpers.windows.singles

import com.example.driverchecker.machinelearning.data.*
import kotlin.time.ExperimentalTime
import kotlin.time.TimeSource

interface IClassificationWindowOld<E : IClassificationOutputStats<S>, S> : IMachineLearningWindowOld<E> {
    val supergroupCounter: Map<S, Int>
    val groupMetrics: IGroupMetrics<S>

    override fun getData() : Pair<IWindowBasicData, IGroupMetrics<S>?>

    override fun getAdditionalMetrics() : IGroupMetrics<S>?

    fun getFinalGroup() : S

    fun updateGroups (newGroups: Set<S>)


    @OptIn(ExperimentalTime::class)
    fun initialize(
        settings: IOldSettings,
        newStart: TimeSource.Monotonic.ValueTimeMark?,
        supergroups: Set<S>
    )
}
