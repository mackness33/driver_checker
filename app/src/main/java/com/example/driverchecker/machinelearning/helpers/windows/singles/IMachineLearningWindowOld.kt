package com.example.driverchecker.machinelearning.helpers.windows.singles

import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.machinelearning.helpers.windows.IWindow
import kotlin.time.ExperimentalTime
import kotlin.time.TimeSource

interface IMachineLearningWindowOld<E : IMachineLearningOutputStats> : IWindow<E>, IWindowSettings {
    @OptIn(ExperimentalTime::class)
    fun initialize(
        settings: IOldSettings, newStart: TimeSource.Monotonic.ValueTimeMark?
    )

    fun getData() : Pair<IWindowBasicData, IAdditionalMetrics?>

    fun getMetrics() : IWindowBasicData

    fun getAdditionalMetrics() : IAdditionalMetrics?
}
