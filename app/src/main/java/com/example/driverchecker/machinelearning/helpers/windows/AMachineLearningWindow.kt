package com.example.driverchecker.machinelearning.helpers.windows

import com.example.driverchecker.machinelearning.data.WithConfidence
import com.example.driverchecker.utils.ISettings

abstract class AMachineLearningWindow<E : WithConfidence> (
    initialSize: Int = 3,
    initialThreshold: Float = 0.15f
) : IMachineLearningWindow<E> {

    protected val window : MutableList<E> = mutableListOf()

    override var size: Int = initialSize
        protected set

    override var threshold: Float = initialThreshold
        protected set

    override var confidence: Float = 0f
        protected set

    override var hasAcceptedLast: Boolean = false
        protected set

    override var totEvaluationsDone: Int = 0
        protected set

    override var lastResult : E? = null
        protected set

    override fun totalWindowsDone() : Int = if (window.size >= size) window.size else 0

    override fun isSatisfied() : Boolean = (window.size == size && threshold <= confidence)

    override fun updateSize (newSize: Int) {
        size = newSize
    }

    override fun updateThreshold (newThreshold: Float) {
        threshold = newThreshold
    }

    override fun updateSettings (settings: ISettings) {
        threshold = settings.windowThreshold
        size = settings.windowFrames
    }

    override fun next (element: E) {
        window.add(element)

        if (window.size > size)
            window.removeFirst()

        update()

        lastResult = element
        totEvaluationsDone++
        hasAcceptedLast = true
    }

    // TODO: Clean also the last evaluation done
    override fun clean () {
        window.clear()
        confidence = 0f
        totEvaluationsDone = 0
        hasAcceptedLast = false
        lastResult = null
    }

    // TODO: Change the calculation of the confidence
    protected open fun update () {
        if (window.size == 0) {
            confidence = 0.0f
        }

        confidence = window.fold(0.0f) { acc, next -> acc + next.confidence } / window.size
    }
}