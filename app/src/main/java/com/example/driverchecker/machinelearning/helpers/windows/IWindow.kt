package com.example.driverchecker.machinelearning.helpers.windows

import com.example.driverchecker.machinelearning.data.*

interface IWindow <E : IMachineLearningOutputStats> : IMachineLearningFinalResultStats {
    val hasAcceptedLast: Boolean

    val totEvaluationsDone: Int

    val lastResult: E?

    fun isSatisfied() : Boolean

    fun next (element: E, offset: Double?)

    suspend fun clean ()

    fun getFinalResults() : IMachineLearningFinalResult

    /* OLD */
    fun getOldFinalResults() : IMachineLearningFinalResultStats

    fun getOldMetrics() : IWindowOldMetrics

    fun getOldFullMetrics() : Pair<IWindowOldMetrics, IAdditionalMetrics?>
}