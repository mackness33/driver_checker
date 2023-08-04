package com.example.driverchecker.machinelearning.helpers.windows

import com.example.driverchecker.machinelearning.data.IMachineLearningFinalResult
import com.example.driverchecker.machinelearning.data.MachineLearningFinalResult
import com.example.driverchecker.machinelearning.data.WithConfidence

open class MachineLearningWindow<Result : WithConfidence> (open val size: Int = 3, open val threshold: Float = 0.15f) :
    IMachineLearningWindow<Result> {
    protected val window : MutableList<Result> = mutableListOf()

    override var confidence: Float = 0f
        protected set

    override var hasAcceptedLast: Boolean = false
        protected set

    override var totEvaluationsDone: Int = 0
        protected set

    override var last : Result? = null
        protected set


    override fun totalNumber() : Int = if (window.size >= size) window.size else 0

    override fun isSatisfied() : Boolean = (window.size == size && threshold <= confidence)

    override fun next (element: Result) {
        window.add(element)

        if (window.size > size)
            window.removeFirst()

        last = element
        totEvaluationsDone++

        update()
        hasAcceptedLast = true
    }

    // TODO: Clean also the last evaluation done
    override fun clean () {
        window.clear()
        confidence = 0f
        totEvaluationsDone = 0
    }

    // TODO: Change the calculation of the confidence
    protected open fun update () {
        if (window.size == 0) {
            confidence = 0.0f
        }

        confidence = window.fold(0.0f) { acc, next -> acc + next.confidence } / window.size
    }

    override fun getIndex(): Int = totEvaluationsDone

    override fun getLastResult(): Result? = last

    override fun getFinalResults() : IMachineLearningFinalResult {
        return MachineLearningFinalResult(confidence)
    }
}