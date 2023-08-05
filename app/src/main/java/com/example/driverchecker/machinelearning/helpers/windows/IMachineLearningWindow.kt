package com.example.driverchecker.machinelearning.helpers.windows

import com.example.driverchecker.machinelearning.data.WithConfidence

interface IMachineLearningWindow<E : WithConfidence> : WithConfidence {
    val lastResult : E?

    val hasAcceptedLast: Boolean

    val totEvaluationsDone: Int

    fun totalWindowsDone() : Int

    fun isSatisfied() : Boolean

    fun next (element: E)

    fun clean ()

    fun getFinalResults() : WithConfidence
}
