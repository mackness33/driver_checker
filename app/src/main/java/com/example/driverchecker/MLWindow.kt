package com.example.driverchecker

import com.example.driverchecker.machinelearning.data.*

open class MLWindow<Data, Prediction, Result : MachineLearningArrayListOutput<Data, Prediction>> (val size: Int = 3, val threshold: Float = 0.15f) : MLWindowInterface<Result> {
    protected val window : MutableList<Result> = mutableListOf()

    var confidence: Float = 0f
        protected set

    var index: Int = 0
        protected set

    var lastResult : Result? = null
        protected set

    override fun totalNumber() : Int = if (window.size >= size) window.size else 0

    override fun isSatisfied() : Boolean = (window.size == size && threshold <= confidence)

    override fun next (element: Result) {
        window.add(element)

        if (window.size > size)
            window.removeFirst()

        lastResult = element
        index++

        confidence = calculateConfidence()
        metricsCalculation()
    }

    override fun clean () {
        window.clear()
        confidence = 0f
        index = 0
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
}