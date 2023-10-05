package com.example.driverchecker.machinelearning.helpers.windows.multiples

import android.util.Log
import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.machinelearning.helpers.windows.IWindow
import com.example.driverchecker.machinelearning.helpers.windows.singles.IMachineLearningSingleWindow
import com.example.driverchecker.machinelearning.helpers.windows.singles.ISingleWindow
import com.example.driverchecker.machinelearning.helpers.windows.singles.ImageDetectionSingleWindow
import com.example.driverchecker.utils.DeferrableData
import com.example.driverchecker.utils.MutableCompletableData
import kotlinx.coroutines.CoroutineScope

open class MachineLearningMultipleWindows<E : IMachineLearningOutputStats, W : IMachineLearningSingleWindow<E>>(scope: CoroutineScope) :
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
