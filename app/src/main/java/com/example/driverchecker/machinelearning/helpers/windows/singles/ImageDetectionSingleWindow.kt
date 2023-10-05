package com.example.driverchecker.machinelearning.helpers.windows.singles

import com.example.driverchecker.machinelearning.data.*
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
open class ImageDetectionSingleWindow (
    initialSettings: IWindowSettings? = null,
    supergroups: Set<String>,
) : AClassificationSingleWindow<IImageDetectionFullOutput<String>, String>(initialSettings, supergroups) {
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