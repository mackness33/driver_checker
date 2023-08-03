package com.example.driverchecker.machinelearning.helpers.windows

import com.example.driverchecker.machinelearning.data.IMachineLearningFinalResult
import com.example.driverchecker.machinelearning.data.MachineLearningFinalResult
import com.example.driverchecker.machinelearning.data.MachineLearningResult
import com.example.driverchecker.machinelearning.data.WithConfidence

open class ClassificationWindow<Result : WithConfidence> (open val size: Int = 3, open val threshold: Float = 0.15f) :
    IMachineLearningWindow<Result> {
    protected val window : MutableList<Result> = mutableListOf()

    var confidence: Float = 0f
        protected set

    protected var numEvaluationDone: Int = 0

    protected var last : Result? = null

    override fun totalNumber() : Int = if (window.size >= size) window.size else 0

    override fun isSatisfied() : Boolean = (window.size == size && threshold <= confidence)

    override fun next (element: Result) {
        window.add(element)

        if (window.size > size)
            window.removeFirst()

        last = element
        numEvaluationDone++

        confidence = calculateConfidence()
        metricsCalculation()
    }

    // TODO: Clean also the last evaluation done
    override fun clean () {
        window.clear()
        confidence = 0f
        numEvaluationDone = 0
    }

    // TODO: Change the calculation of the confidence
    protected open fun calculateConfidence () : Float {
        if (window.size == 0) {
            return 0.0f
        }
        return window.fold(0.0f) { acc, next -> acc + next.confidence } / window.size
    }

    // Is gonna return the confidence and other metrics
    open fun metricsCalculation () {}

    override fun getIndex(): Int = numEvaluationDone

    override fun getLastResult(): Result? = last

    override fun getFinalResults() : IMachineLearningFinalResult {
        return MachineLearningFinalResult(confidence)
    }
}