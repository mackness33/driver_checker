package com.example.driverchecker.machinelearning.helpers.windows

import com.example.driverchecker.machinelearning.data.*
import kotlin.time.ExperimentalTime
import kotlin.time.TimeSource

@OptIn(ExperimentalTime::class)
open class ClassificationWindow<E : IClassificationOutputStats<S>, S> (
    size: Int = 3,
    threshold: Float = 0.15f,
    supergroups: Set<S>,
    override val type: String = "ClassificationWindow",
    newStart: TimeSource.Monotonic.ValueTimeMark? = null
) : AClassificationWindow<E, S>(size, threshold, supergroups = supergroups, newStart = newStart) {
    override fun getOldFinalResults() : IClassificationFinalResultStats<S> {
        return ClassificationFullFinalResultOld(confidence, supergroupCounter.maxWith { o1, o2 -> o1.value.compareTo(o2.value) }.key)
    }
}