package com.example.driverchecker.machinelearning.helpers.windows

import com.example.driverchecker.machinelearning.data.*
import kotlin.time.ExperimentalTime
import kotlin.time.TimeSource

@OptIn(ExperimentalTime::class)
open class ImageDetectionSingleWindow (
    initialSettings: IWindowSettings? = null,
    supergroups: Set<String>,
) : AClassificationSingleWindow<IImageDetectionOutputStats<String>, String> (initialSettings, supergroups) {
    override fun getMetrics(): IWindowBasicData {
        return WindowBasicData(
            totalTime,
            totalWindows,
            averageTime,
            averageConfidence,
            confidence,
            group!!,
            size,
            threshold,
            type,
        )
    }

    override val type: String
        get() = "Image Detection Window"

    override fun getFinalResults(): IClassificationFinalResult<String> {
        return ImageDetectionFinalResult(confidence, group!!, mapOf(getData()), 0.0f)
    }
}