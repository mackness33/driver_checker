package com.example.driverchecker.machinelearning.collections

import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.utils.ObservableData
import com.example.driverchecker.machinelearning.helpers.windows.IMachineLearningWindow
import com.example.driverchecker.machinelearning.helpers.windows.IWindow

interface MachineLearningItemList<E : IMachineLearningItem> : List<E>, IMachineLearningOutputStats
interface ClassificationItemList<E : IClassificationItem<S>, S> : MachineLearningItemList<E>,
    IClassificationOutputStats<S>

interface ClassificationMetricsMap<S> : IGroupMetrics<S> {
    val liveMetrics: Map<S, ObservableData<Triple<Int, Int, Int>>>
}

interface MachineLearningWindowsCollection <E : IMachineLearningOutputStats, W : IMachineLearningWindow<E>> : IWindow<E>, Collection<W> {
    val inactiveWindows: Set<W>
    val activeWindows: Set<W>
    val settings: IMultipleWindowSettings

    fun getData() : Map<IWindowBasicData, IAdditionalMetrics?>

    fun getMetrics() : List<IWindowBasicData>

    fun getAdditionalMetrics() : List<IAdditionalMetrics?>
}

interface MachineLearningWindowsMutableCollection <E : IMachineLearningOutputStats, W : IMachineLearningWindow<E>> : MachineLearningWindowsCollection<E, W>, MutableCollection<W> {
    fun updateSettings (newSettings: IMultipleWindowSettings)
}