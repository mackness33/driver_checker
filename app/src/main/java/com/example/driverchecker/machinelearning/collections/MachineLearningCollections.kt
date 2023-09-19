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

interface ClassificationWindowsCollection <E : IClassificationOutputStats<S>, W : IClassificationWindow<E, S>, S> : MachineLearningWindowsCollection<E, W> {
    val groups: Set<S>

    override fun getData() : Map<IWindowBasicData, IGroupMetrics<S>?>

    override fun getAdditionalMetrics() : List<IGroupMetrics<S>?>

    override fun getFinalResults(): IClassificationFinalResult<S>
}

interface ClassificationWindowsMutableCollection <E : IClassificationOutputStats<S>, W : IClassificationWindow<E, S>, S> : ClassificationWindowsCollection<E, W, S>, MachineLearningWindowsMutableCollection<E, W> {
    fun updateGroups (newGroups: Set<S>)
}