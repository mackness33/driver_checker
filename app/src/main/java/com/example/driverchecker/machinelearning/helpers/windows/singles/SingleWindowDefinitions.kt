package com.example.driverchecker.machinelearning.helpers.windows.singles

import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.machinelearning.helpers.windows.IWindow
import com.example.driverchecker.machinelearning.helpers.windows.helpers.IWindowTag

interface ISingleWindow <E> : IWindow<E> {
    val settings: IWindowSettingsOld
    val totalWindows: Int
    val totalTime: Double
    val lastTime: Double
    val tag: IWindowTag

    fun updateSettings(newSettings: IWindowSettingsOld)
}

interface IMachineLearningSingleWindow <E : IMachineLearningOutputStatsOld> : ISingleWindow<E> {
    val threshold: Float

    fun getData() : Pair<IWindowBasicData, IAdditionalMetrics?>
    fun getMetrics() : IWindowBasicData
    fun getAdditionalMetrics() : IAdditionalMetrics?
    fun getFinalResults(): IMachineLearningFinalResultOld
}

interface IClassificationSingleWindow <E : IClassificationOutputStatsOld<S>, S> :
    IMachineLearningSingleWindow<E> {
    val supergroupCounter: Map<S, Int>
    val groupMetrics: IGroupMetrics<S>

    fun updateGroups (newGroups: Set<S>)

    /* MACHINE LEARNING */
    override fun getData() : Pair<IWindowBasicData, IGroupMetrics<S>?>
    override fun getAdditionalMetrics() : IGroupMetrics<S>?
    override fun getFinalResults(): IClassificationFinalResultOld<S>
}