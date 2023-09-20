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
) : IMachineLearningWindow<E>, WithConfidence, IWindowBasicData {

    protected val window : MutableList<E> = mutableListOf()

    protected val timer: Timer = Timer(newStart)

    override var windowFrames: Int = initialSize
        protected set

    override var windowThreshold: Float = initialThreshold
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
        protected set

    override var totalWindows: Int = 0
        get() = if (window.size >= windowFrames) (totEvaluationsDone + 1) - window.size else 0
        protected set

    override fun isSatisfied() : Boolean {
      return (window.size == windowFrames && windowThreshold <= confidence)
    }

    override fun initialize(settings: IOldSettings, start: TimeSource.Monotonic.ValueTimeMark?) {
        windowFrames = settings.windowFrames
        windowThreshold = settings.windowThreshold
        timer.initStart(start)
    }

    final override fun next(element: E, offset: Double?) {
        timer.markStart()

        hasAcceptedLast = preUpdate(element)
        if (!hasAcceptedLast)
            return

        update()

        postUpdate()

        timer.markEnd()

        val totalOutputTime: Double = timer.diff()?.plus((offset ?: 0.0)) ?: 0.0
        totalTime += totalOutputTime
    }

    protected open fun preUpdate (element: E) : Boolean {
        window.add(element)

        if (window.size > windowFrames)
            window.removeFirst()

        return true
    }

    protected open fun postUpdate () {
        totEvaluationsDone++
        hasAcceptedLast = true
    }

    override suspend fun clean () {
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

    /* DATA */
    override fun getData(): Pair<IWindowBasicData, IAdditionalMetrics?> {
        return getMetrics() to getAdditionalMetrics()
    }

    override fun getMetrics(): IWindowBasicData {
        return WindowBasicData(this)
    }

    override fun getAdditionalMetrics(): IAdditionalMetrics? {
        return null
    }

    override fun getFinalResults(): IMachineLearningFinalResult {
        return MachineLearningFinalResult(confidence, mapOf(getData()))
    }

    /* OLD */
    override fun getOldMetrics() : IWindowOldMetrics {
        return WindowOldMetrics(totalTime, totalWindows, type)
    }

    override fun getOldFullMetrics() : Pair<IWindowOldMetrics, IAdditionalMetrics?> {
        return Pair(getOldMetrics(), null)
    }

    override fun updateStart(newStart: TimeSource.Monotonic.ValueTimeMark) {
        timer.initStart(newStart)
    }
}