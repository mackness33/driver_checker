package com.example.driverchecker.machinelearning.windows.multiples

import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.machinelearning.windows.IWindow

interface IMultipleWindows <E> : IWindow<E> {
    val activeWindows: Set<IWindow<E>>
    val inactiveWindows: Set<IWindow<E>>

    fun <M : IMultipleWindowSettings> update (newSettings: M)
}

interface IMachineLearningMultipleWindows <E : IMachineLearningOutput> : IMultipleWindows<E> {
    fun getData() : Map<IWindowBasicData, IAdditionalMetrics?>
    fun getMetrics() : List<IWindowBasicData>
    fun getAdditionalMetrics() : List<IAdditionalMetrics?>
    fun getFinalResults(): IMachineLearningFinalResult
}

interface IClassificationMultipleWindows <E : IClassificationOutput<S>, S> :
    IMachineLearningMultipleWindows<E> {
    val groups: Set<S>

    fun updateGroups (newGroups: Set<S>)

    /* MACHINE LEARNING */
    override fun getData() : Map<IWindowBasicData, IGroupMetrics<S>?>
    override fun getAdditionalMetrics() : List<IGroupMetrics<S>?>
    override fun getFinalResults(): IClassificationFinalResult<S>
}
