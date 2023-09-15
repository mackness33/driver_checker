package com.example.driverchecker.machinelearning.helpers.windows

import com.example.driverchecker.machinelearning.data.IMachineLearningFinalResult
import com.example.driverchecker.machinelearning.data.IMachineLearningOutputStats
import com.example.driverchecker.machinelearning.data.WithConfidence
import com.example.driverchecker.utils.ISettings

interface IMachineLearningWindow<E : IMachineLearningOutputStats> : WithConfidence {
    val lastResult : E?

    val hasAcceptedLast: Boolean

    val totEvaluationsDone: Int

    val size: Int

    val threshold: Float

    fun totalWindowsDone() : Int

    fun isSatisfied() : Boolean

    fun next (element: E)

    fun clean ()

    fun getFinalResults() : IMachineLearningFinalResult

    fun updateSize(newSize: Int)

    fun updateThreshold(newThreshold: Float)

    fun updateSettings(settings: ISettings)
}
