package com.example.driverchecker.machinelearning.helpers.windows

import com.example.driverchecker.machinelearning.data.*
import kotlin.time.ExperimentalTime
import kotlin.time.TimeSource

interface IMachineLearningWindow<E : IMachineLearningOutputStats> : IWindow<E>, IWindowSettings {
    @OptIn(ExperimentalTime::class)
    fun initialize(
        settings: IOldSettings, newStart: TimeSource.Monotonic.ValueTimeMark?
    )

    fun getData() : Pair<IWindowBasicData, IAdditionalMetrics?>

    fun getMetrics() : IWindowBasicData

    fun getAdditionalMetrics() : IAdditionalMetrics?

    @OptIn(ExperimentalTime::class)
    fun updateStart (newStart: TimeSource.Monotonic.ValueTimeMark)
}
