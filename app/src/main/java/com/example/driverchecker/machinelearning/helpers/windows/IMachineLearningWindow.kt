package com.example.driverchecker.machinelearning.helpers.windows

import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.utils.ISettings
import kotlin.time.ExperimentalTime
import kotlin.time.TimeSource

interface IMachineLearningWindow<E : IMachineLearningOutputStats> {
    val hasAcceptedLast: Boolean

    val totEvaluationsDone: Int

    val size: Int

    val threshold: Float

    val lastResult: E?

    @OptIn(ExperimentalTime::class)
    fun initialize(
        settings: ISettings, newStart: TimeSource.Monotonic.ValueTimeMark?
    )

    fun isSatisfied() : Boolean

    fun next (element: E)

    fun clean ()

    fun getFinalResults() : IMachineLearningFinalResultStats

    fun getMetrics() : IWindowMetrics

    fun getFullMetrics() : Pair<IWindowMetrics, IAdditionalMetrics?>

    @OptIn(ExperimentalTime::class)
    fun updateStart (newStart: TimeSource.Monotonic.ValueTimeMark)
}
