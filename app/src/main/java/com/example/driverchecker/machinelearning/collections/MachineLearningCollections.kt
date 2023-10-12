package com.example.driverchecker.machinelearning.collections

import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.utils.ObservableData
import com.example.driverchecker.machinelearning.helpers.windows.IWindow

interface MachineLearningItemListOld<E : IMachineLearningItem> : List<E>, IMachineLearningOutputStatsOld
interface ClassificationItemListOld<E : IClassificationItem<S>, S> : MachineLearningItemListOld<E>,
    IClassificationOutputStatsOld<S>

interface ClassificationMetricsMap<S> : IGroupMetrics<S> {
    val liveMetrics: Map<S, ObservableData<Triple<Int, Int, Int>>>
}

interface MachineLearningWindowsCollection <E : IMachineLearningOutputStatsOld> : IWindow<E> {
    val settings: IMultipleWindowSettingsOld

    fun getData() : Map<IWindowBasicData, IAdditionalMetrics?>

    fun getMetrics() : List<IWindowBasicData>

    fun getAdditionalMetrics() : List<IAdditionalMetrics?>
    fun getFinalResults(): IMachineLearningFinalResultOld
    fun initialize(availableSettings: ISettingsOld)
}

interface MachineLearningWindowsMutableCollection <E : IMachineLearningOutputStatsOld> :
    MachineLearningWindowsCollection<E> {
    fun updateSettings (newSettings: ISettingsOld)
}

interface ClassificationWindowsCollection <E : IClassificationOutputStatsOld<S>, S> :
    MachineLearningWindowsCollection<E> {
    val groups: Set<S>

    override fun getData() : Map<IWindowBasicData, IGroupMetrics<S>?>

    override fun getAdditionalMetrics() : List<IGroupMetrics<S>?>

    override fun getFinalResults(): IClassificationFinalResultOld<S>
}

interface ClassificationWindowsMutableCollection <E : IClassificationOutputStatsOld<S>, S> :
    ClassificationWindowsCollection<E, S>, MachineLearningWindowsMutableCollection<E> {
    fun updateGroups (newGroups: Set<S>)
}