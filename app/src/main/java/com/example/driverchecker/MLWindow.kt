package com.example.driverchecker

import com.example.driverchecker.machinelearning.data.*

open class MLWindow<Data, Prediction, Superclass, Result : MachineLearningArrayListOutput<Data, Prediction, Superclass>> (val size: Int = 3, val threshold: Float = 0.15f) : MLWindowInterface<Result> {
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

    protected open fun calculateConfidence () : Float {
        var sum = 0f
        for (prediction in window) {
            var sumPrediction = 0f
            for (box in prediction) {
                sumPrediction += box.confidence
            }
            sum += (sumPrediction / prediction.size)
        }

        return sum / window.size
    }

    // Is gonna return the confidence and other metrics
    open fun metricsCalculation () {}

    override fun getIndex(): Int = numEvaluationDone

    override fun getLastResult(): Result? = last
}