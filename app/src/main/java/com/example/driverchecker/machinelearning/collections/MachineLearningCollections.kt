package com.example.driverchecker.machinelearning.collections

import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.utils.ObservableData
import com.example.driverchecker.machinelearning.windows.IWindow

interface ClassificationMetricsMap<S> : IGroupMetrics<S> {
    val liveMetrics: Map<S, ObservableData<Triple<Int, Int, Int>>>
}