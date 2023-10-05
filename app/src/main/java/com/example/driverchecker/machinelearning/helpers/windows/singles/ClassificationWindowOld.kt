package com.example.driverchecker.machinelearning.helpers.windows.singles

import com.example.driverchecker.machinelearning.data.*
import kotlin.time.ExperimentalTime
import kotlin.time.TimeSource

@OptIn(ExperimentalTime::class)
open class ClassificationWindowOld<E : IClassificationOutputStats<S>, S> (
    size: Int = 3,
    threshold: Float = 0.15f,
    supergroups: Set<S>,
    override val type: String = "ClassificationWindow",
    newStart: TimeSource.Monotonic.ValueTimeMark? = null
) : AClassificationWindowOld<E, S>(size, threshold, supergroups = supergroups, newStart = newStart) {
    override val supergroup: String
        get() = ""

    override fun getMetrics(): IWindowBasicData {
        return WindowBasicData(this)
    }
}