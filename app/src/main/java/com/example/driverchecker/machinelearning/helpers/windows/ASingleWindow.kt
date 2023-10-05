package com.example.driverchecker.machinelearning.helpers.windows

import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.utils.Timer
import kotlin.time.ExperimentalTime
import kotlin.time.TimeSource

abstract class ASingleWindow<E> (initialSize: Int = 0) : ISingleWindow<E> {
    protected val window : MutableList<E> = mutableListOf()
    protected val timer: Timer = Timer()

    var size: Int = initialSize
        protected set
    override var hasAcceptedLast: Boolean = false
        protected set

    protected var windowMetrics: IWindowMetrics? = null
    protected var windowSettings: IWindowSettings? = null

    override var totalElements: Int = 0
        protected set
    override var totalTime: Double = 0.0
        protected set
    override val totalWindows: Int
        get() = if (window.size >= size) (totalElements + 1) - window.size else 0

    override val lastResult: E?
        get() = if (window.isEmpty()) null else window.last()
    override var lastTime: Double = 0.0
        protected set

    final override fun next(element: E, timeOffset: Double?) {
        timer.markStart()

        hasAcceptedLast = preUpdate(element)
        if (!hasAcceptedLast){
            finally(timeOffset)
            return
        }

        update()
        postUpdate()
        finally(timeOffset)
    }

    protected open fun preUpdate (element: E) : Boolean {
        window.add(element)

        if (window.size > size)
            window.removeFirst()

        return true
    }

    protected open fun postUpdate () {
        totalElements++
    }

    protected open fun finally (timeOffset: Double?) {
        timer.markEnd()

        lastTime = timer.diff()?.plus((timeOffset ?: 0.0)) ?: 0.0
        totalTime += lastTime
    }

    override suspend fun clean () {
        window.clear()
        totalElements = 0
        hasAcceptedLast = false
        totalTime = 0.0
        lastTime = 0.0
        timer.reset()
    }

    protected abstract fun update ()
}