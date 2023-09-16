package com.example.driverchecker.machinelearning.helpers.windows

import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.utils.ISettings

interface IMachineLearningWindow<E : IMachineLearningOutputStats> : WithConfidence, IWindowMetrics {
    val lastResult : E?

    val hasAcceptedLast: Boolean

    val totEvaluationsDone: Int

    val size: Int

    val threshold: Float

    fun isSatisfied() : Boolean

    fun next (element: E)

    fun clean ()

    fun getFinalResults() : IMachineLearningFinalResultStats

    fun getMetrics() : IWindowMetrics

    fun getFullMetrics() : Pair<IWindowMetrics, IAdditionalMetrics?>

    fun updateSize(newSize: Int)

    fun updateThreshold(newThreshold: Float)

    fun updateSettings(settings: ISettings)
}
