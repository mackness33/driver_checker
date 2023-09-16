package com.example.driverchecker.utils

import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.TimeSource

@OptIn(ExperimentalTime::class)
class Timer {
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

    fun diff() : Double? {
        return if (start == null || end == null)
            null
        else
            (end!! - start!!).toDouble(DurationUnit.SECONDS)
    }
}