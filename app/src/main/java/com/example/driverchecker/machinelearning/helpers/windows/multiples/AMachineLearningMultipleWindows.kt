package com.example.driverchecker.machinelearning.helpers.windows.multiples

import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.machinelearning.helpers.windows.factories.IMachineLearningWindowFactory
import com.example.driverchecker.machinelearning.helpers.windows.singles.IMachineLearningSingleWindow
import kotlinx.coroutines.CoroutineScope

abstract class AMachineLearningMultipleWindows<E : IMachineLearningOutputStatsOld, W : IMachineLearningSingleWindow<E>, S : IMachineLearningSingleWindowSettings>(scope: CoroutineScope) :
    AMultipleWindows<E, W, S> (scope), IMachineLearningMultipleWindows<E> {
    abstract override val factory: IMachineLearningWindowFactory<E, S, W>

    /* MACHINE LEARNING */
    override val confidence: Float
        get() = 0.0f

    /* DATA */
    override fun getMetrics(): List<IWindowBasicData> {
        return currentWindows.values.map { it.getMetrics() }
    }

    override fun getAdditionalMetrics(): List<IAdditionalMetrics?> {
        return currentWindows.values.map { it.getAdditionalMetrics() }
    }

    override fun getData(): Map<IWindowBasicData, IAdditionalMetrics?> {
        val listOfData = currentWindows.values.map { it.getData() }
        return listOfData.toMap()
    }
}
