package com.example.driverchecker.machinelearning.helpers.windows

import com.example.driverchecker.machinelearning.data.IAdditionalMetrics
import com.example.driverchecker.machinelearning.data.IMachineLearningOutputStats
import com.example.driverchecker.machinelearning.data.IWindowMetrics
import com.example.driverchecker.machinelearning.data.WindowMetrics
import com.example.driverchecker.utils.ISettings
import com.example.driverchecker.utils.Timer
import kotlin.time.ExperimentalTime
import kotlin.time.TimeSource

@OptIn(ExperimentalTime::class)
abstract class AMachineLearningWindow<E : IMachineLearningOutputStats> constructor(
    initialSize: Int = 3,
    initialThreshold: Float = 0.15f,
    newStart: TimeSource.Monotonic.ValueTimeMark? = null
) : IMachineLearningWindow<E> {

    protected val window : MutableList<E> = mutableListOf()

    protected val timer: Timer = Timer(newStart)

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

    override var totalTime: Double = 0.0
        get() = timer.diff() ?: 0.0
        protected set

    override var totalWindows: Int = 0
        get() = if (window.size >= size) (totEvaluationsDone + 1) - window.size else 0
        protected set

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

    override fun clean () {
        window.clear()
        confidence = 0f
        totEvaluationsDone = 0
        hasAcceptedLast = false
        lastResult = null
        totalWindows = 0
        totalTime = 0.0
        timer.reset()
    }

    protected open fun update () {
        if (window.size == 0) {
            confidence = 0.0f
        }

        confidence = window.fold(0.0f) { acc, next -> acc + next.confidence } / window.size
    }


    override fun getMetrics() : IWindowMetrics {
        return WindowMetrics(totalTime, totalWindows, type)
    }

    override fun getFullMetrics() : Pair<IWindowMetrics, IAdditionalMetrics?> {
        return Pair(getMetrics(), null)
    }
}