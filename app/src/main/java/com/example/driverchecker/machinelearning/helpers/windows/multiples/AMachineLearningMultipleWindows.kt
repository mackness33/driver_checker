package com.example.driverchecker.machinelearning.helpers.windows.multiples

import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.machinelearning.helpers.windows.singles.IMachineLearningSingleWindow
import kotlinx.coroutines.CoroutineScope

abstract class AMachineLearningMultipleWindows<E : IMachineLearningOutputStats, W : IMachineLearningSingleWindow<E>>(scope: CoroutineScope) :
    AMultipleWindows<E, W> (scope), IMachineLearningMultipleWindows<E> {
    /* MACHINE LEARNING */
    override val confidence: Float
        get() = 0.0f

    /* DATA */
    override fun getMetrics(): List<IWindowBasicData> {
        return selectedWindows.map { it.getMetrics() }
    }

    override fun getAdditionalMetrics(): List<IAdditionalMetrics?> {
        return selectedWindows.map { it.getAdditionalMetrics() }
    }

    override fun getData(): Map<IWindowBasicData, IAdditionalMetrics?> {
        val listOfData = selectedWindows.map { it.getData() }
        return listOfData.toMap()
    }

    override val type: String
        get() = "MultipleWindows"
}
