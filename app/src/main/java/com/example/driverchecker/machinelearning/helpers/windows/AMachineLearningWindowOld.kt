package com.example.driverchecker.machinelearning.helpers.windows

import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.utils.Timer
import kotlin.time.ExperimentalTime
import kotlin.time.TimeSource

@OptIn(ExperimentalTime::class)
abstract class AMachineLearningWindowOld<E : IMachineLearningOutputStats> constructor(
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

    override var totalElements: Int = 0
        protected set

    override val lastResult: E?
        get() = if (window.isEmpty()) null else window.last()

    override var totalTime: Double = 0.0
        protected set

    override val totalWindows: Int
        get() = if (window.size >= windowFrames) (totalElements + 1) - window.size else 0

    override val averageTime: Double
        get() = totalTime/totalWindows

    protected var sumOfConfidencePerWindowDone: Float = 0.0f
    override val averageConfidence: Float
        get() = sumOfConfidencePerWindowDone/totalWindows

    override fun isSatisfied() : Boolean {
      return (window.size == windowFrames && windowThreshold <= confidence)
    }

    override fun initialize(settings: IOldSettings, start: TimeSource.Monotonic.ValueTimeMark?) {
        windowFrames = settings.windowFrames
        windowThreshold = settings.windowThreshold
        timer.initStart(start)
    }

    final override fun next(element: E, timeOffset: Double?) {
        timer.markStart()

        hasAcceptedLast = preUpdate(element)
        if (!hasAcceptedLast)
            return

        update()

        postUpdate()

        timer.markEnd()

        val totalOutputTime: Double = timer.diff()?.plus((timeOffset ?: 0.0)) ?: 0.0
        totalTime += totalOutputTime
    }

    protected open fun preUpdate (element: E) : Boolean {
        window.add(element)

        if (window.size > windowFrames)
            window.removeFirst()

        return true
    }

    protected open fun postUpdate () {
        totalElements++
        hasAcceptedLast = true
        if (window.size == windowFrames) sumOfConfidencePerWindowDone += confidence

    }

    override suspend fun clean () {
        window.clear()
        confidence = 0f
        totalElements = 0
        hasAcceptedLast = false
//        totalWindows = 0
        totalTime = 0.0
        sumOfConfidencePerWindowDone = 0.0f
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

    override fun getAdditionalMetrics(): IAdditionalMetrics? {
        return null
    }
}