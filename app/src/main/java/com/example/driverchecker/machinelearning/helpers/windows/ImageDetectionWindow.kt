package com.example.driverchecker.machinelearning.helpers.windows

import com.example.driverchecker.machinelearning.data.*
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.TimeSource

@OptIn(ExperimentalTime::class)
open class BasicImageDetectionWindow (
    size: Int = 3,
    threshold: Float = 0.15f,
    supergroups: Set<String>,
    override val type: String = "ClassificationWindow",
    newStart: TimeSource.Monotonic.ValueTimeMark? = null
) : ImageDetectionWindow (size, threshold, supergroups = supergroups, newStart = newStart) {
    override fun getFinalResults() : IClassificationFinalResultStats<String> {
        return ImageDetectionFullFinalResult(confidence, supergroupCounter.maxWith { o1, o2 -> o1.value.compareTo(o2.value) }.key)
    }

    companion object Factory : ImageDetectionWindowFactory (){

        override fun buildWindow(): ImageDetectionWindow {
            return ImageDetectionWindow(4, 0.80f, emptySet())
        }
    }
}