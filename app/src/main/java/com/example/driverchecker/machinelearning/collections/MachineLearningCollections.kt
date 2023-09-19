package com.example.driverchecker.machinelearning.collections

import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.machinelearning.helpers.windows.IClassificationWindow
import com.example.driverchecker.utils.ObservableData
import com.example.driverchecker.machinelearning.helpers.windows.IMachineLearningWindow
import com.example.driverchecker.machinelearning.helpers.windows.IWindow

interface MachineLearningItemList<E : IMachineLearningItem> : List<E>, IMachineLearningOutputStats
interface ClassificationItemList<E : IClassificationItem<S>, S> : MachineLearningItemList<E>,
    IClassificationOutputStats<S>

interface ClassificationMetricsMap<S> : IGroupMetrics<S> {
    val liveMetrics: Map<S, ObservableData<Triple<Int, Int, Int>>>
}

interface MachineLearningWindowsCollection <E : IMachineLearningOutputStats> : IWindow<E> {
    val settings: IMultipleWindowSettings

    fun getData() : Map<IWindowBasicData, IAdditionalMetrics?>

    fun getMetrics() : List<IWindowBasicData>

    fun getAdditionalMetrics() : List<IAdditionalMetrics?>
    fun initialize(availableSettings: IMultipleWindowSettings)
}

interface MachineLearningWindowsMutableCollection <E : IMachineLearningOutputStats> : MachineLearningWindowsCollection<E> {
    fun updateSettings (newSettings: IMultipleWindowSettings)
}

interface ClassificationWindowsCollection <E : IClassificationOutputStats<S>, S> : MachineLearningWindowsCollection<E> {
    val groups: Set<S>

    override fun getData() : Map<IWindowBasicData, IGroupMetrics<S>?>

    override fun getAdditionalMetrics() : List<IGroupMetrics<S>?>

    override fun getFinalResults(): IClassificationFinalResult<S>
}

interface ClassificationWindowsMutableCollection <E : IClassificationOutputStats<S>, S> : ClassificationWindowsCollection<E, S>, MachineLearningWindowsMutableCollection<E> {
    fun updateGroups (newGroups: Set<S>)
}