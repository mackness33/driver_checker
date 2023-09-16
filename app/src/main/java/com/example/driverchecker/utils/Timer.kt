package com.example.driverchecker.utils

import android.os.health.TimerStat
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.TimeSource

@OptIn(ExperimentalTime::class)
class Timer () {
    constructor(newStart: TimeSource.Monotonic.ValueTimeMark?) : this() {
        start = newStart
    }

    private val timeSource = TimeSource.Monotonic
    var start: TimeSource.Monotonic.ValueTimeMark? = null
        private set
    var end: TimeSource.Monotonic.ValueTimeMark? = null
        private set

    fun reset () {
        start = null
        end = null
    }

    fun markStart() {
        start = timeSource.markNow()
    }

    fun markEnd() {
        end = timeSource.markNow()
    }

    fun initStart(newStart: TimeSource.Monotonic.ValueTimeMark?) {
        start = newStart
    }

    fun diff() : Double? {
        return if (start == null || end == null)
            null
        else
            (end!! - start!!).toDouble(DurationUnit.SECONDS)
    }
}