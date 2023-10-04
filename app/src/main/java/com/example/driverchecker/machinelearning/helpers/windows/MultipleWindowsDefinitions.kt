package com.example.driverchecker.machinelearning.helpers.windows

import com.example.driverchecker.machinelearning.data.*

interface IMultipleWindows <E> : IWindow<E> {
    val settings: ISettings
    val activeWindows: Set<IWindow<E>>
    val inactiveWindows: Set<IWindow<E>>

    fun initialize(availableSettings: ISettings)
    fun updateSettings(newSettings: ISettings)
}

interface IMachineLearningMultipleWindows <E : IMachineLearningOutputStats> : IMultipleWindows<E> {
    fun getData() : Map<IWindowBasicData, IAdditionalMetrics?>
    fun getMetrics() : List<IWindowBasicData>
    fun getAdditionalMetrics() : List<IAdditionalMetrics?>
    fun getFinalResults(): IMachineLearningFinalResult
}

interface IClassificationMultipleWindows <E : IClassificationOutputStats<S>, S> : IMachineLearningMultipleWindows<E> {
    val groups: Set<S>

    fun updateGroups (newGroups: Set<S>)

    /* MACHINE LEARNING */
    override fun getData() : Map<IWindowBasicData, IGroupMetrics<S>?>
    override fun getAdditionalMetrics() : List<IGroupMetrics<S>?>
    override fun getFinalResults(): IClassificationFinalResult<S>
}