package com.example.driverchecker.machinelearning.helpers.windows.multiples

import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.machinelearning.helpers.windows.IWindow

interface IMultipleWindows <E> : IWindow<E> {
    val activeWindows: Set<IWindow<E>>
    val inactiveWindows: Set<IWindow<E>>

    fun <M : IMultipleWindowSettings> update (newSettings: M)
}

interface IMachineLearningMultipleWindows <E : IMachineLearningOutputStats> : IMultipleWindows<E> {
    fun getData() : Map<IWindowBasicData, IAdditionalMetrics?>
    fun getMetrics() : List<IWindowBasicData>
    fun getAdditionalMetrics() : List<IAdditionalMetrics?>
    fun getFinalResults(): IMachineLearningFinalResultOld
}

interface IClassificationMultipleWindows <E : IClassificationOutputStatsOld<S>, S> :
    IMachineLearningMultipleWindows<E> {
    val groups: Set<S>

    fun updateGroups (newGroups: Set<S>)

    /* MACHINE LEARNING */
    override fun getData() : Map<IWindowBasicData, IGroupMetrics<S>?>
    override fun getAdditionalMetrics() : List<IGroupMetrics<S>?>
    override fun getFinalResults(): IClassificationFinalResultOld<S>
}

