package com.example.driverchecker.machinelearning.helpers.windows

import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.utils.Timer
import kotlin.time.ExperimentalTime
import kotlin.time.TimeSource

@OptIn(ExperimentalTime::class)
abstract class AMachineLearningWindow<E : IMachineLearningOutputStats> constructor(
    initialSize: Int = 3,
    initialThreshold: Float = 0.15f,
    newStart: TimeSource.Monotonic.ValueTimeMark? = null
) : IMachineLearningWindow<E>, WithConfidence, IWindowOldMetrics{

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

    override val lastResult: E?
        get() = if (window.isEmpty()) null else window.last()


    override var totalTime: Double = 0.0
        get() = timer.diff() ?: 0.0
        protected set

    override var totalWindows: Int = 0
        get() = if (window.size >= size) (totEvaluationsDone + 1) - window.size else 0
        protected set

    override fun isSatisfied() : Boolean = (window.size == size && threshold <= confidence)

    override fun initialize(settings: IOldSettings, start: TimeSource.Monotonic.ValueTimeMark?) {
        size = settings.windowFrames
        threshold = settings.windowThreshold
        timer.initStart(start)
    }

    final override fun next(element: E, offset: Double?) : TimeSource.Monotonic.ValueTimeMark? {
        timer.markStart()

        hasAcceptedLast = preUpdate(element)
        if (!hasAcceptedLast)
            return null

        update()

        postUpdate()

        timer.markEnd()

        window.last().updateTime(timer.diff())

        return timer.end
    }

    protected open fun preUpdate (element: E) : Boolean {
        window.add(element)

        if (window.size > size)
            window.removeFirst()

        return true
    }

    protected open fun postUpdate () {
        totEvaluationsDone++
        hasAcceptedLast = true
    }

    override fun clean () {
        window.clear()
        confidence = 0f
        totEvaluationsDone = 0
        hasAcceptedLast = false
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


    override fun getMetrics() : IWindowOldMetrics {
        return WindowOldMetrics(totalTime, totalWindows, type)
    }

    override fun getFullMetrics() : Pair<IWindowOldMetrics, IAdditionalMetrics?> {
        return Pair(getMetrics(), null)
    }

    override fun updateStart(newStart: TimeSource.Monotonic.ValueTimeMark) {
        timer.initStart(newStart)
    }
}