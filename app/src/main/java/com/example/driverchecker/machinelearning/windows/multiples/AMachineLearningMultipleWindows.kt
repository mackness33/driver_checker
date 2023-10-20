package com.example.driverchecker.machinelearning.windows.multiples

import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.machinelearning.windows.factories.IMachineLearningWindowFactory
import com.example.driverchecker.machinelearning.windows.singles.IMachineLearningSingleWindow
import kotlinx.coroutines.CoroutineScope

abstract class AMachineLearningMultipleWindows<E : IMachineLearningOutput, W : IMachineLearningSingleWindow<E>, S : IMachineLearningSingleWindowSettings>(scope: CoroutineScope) :
    AMultipleWindows<E, W, S>(scope), IMachineLearningMultipleWindows<E> {
    abstract override val factory: IMachineLearningWindowFactory<E, S, W>
    protected var finalConfidence: Float = 0.0f

    override fun onWindowSatisfied(window: W) {
        finalConfidence += window.confidence
    }

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

    override suspend fun clean() {
        super.clean()
        finalConfidence = 0.0f
    }
}
