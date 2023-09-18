package com.example.driverchecker.machinelearning.helpers.windows

import com.example.driverchecker.machinelearning.data.*
import kotlin.time.ExperimentalTime
import kotlin.time.TimeSource

interface IMachineLearningWindow<E : IMachineLearningOutputStats> {
    val hasAcceptedLast: Boolean

    val totEvaluationsDone: Int

    val size: Int

    val threshold: Float

    val lastResult: E?

    @OptIn(ExperimentalTime::class)
    val end: TimeSource.Monotonic.ValueTimeMark?

    @OptIn(ExperimentalTime::class)
    fun initialize(
        settings: IOldSettings, newStart: TimeSource.Monotonic.ValueTimeMark?
    )

    fun isSatisfied() : Boolean

    fun next (element: E, offset: Double?)

    fun clean ()

    fun getFinalResults() : IMachineLearningFinalResultStats

    fun getMetrics() : IWindowOldMetrics

    fun getFullMetrics() : Pair<IWindowOldMetrics, IAdditionalMetrics?>

    @OptIn(ExperimentalTime::class)
    fun updateStart (newStart: TimeSource.Monotonic.ValueTimeMark)
}
