package com.example.driverchecker.machinelearning.helpers.windows.singles

import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.machinelearning.helpers.windows.factories.AMachineLearningWindowFactory
import com.example.driverchecker.machinelearning.helpers.windows.factories.AWindowFactory
import com.example.driverchecker.machinelearning.helpers.windows.factories.IImageDetectionWindowFactory2
import kotlin.time.ExperimentalTime

open class ImageDetectionSingleWindow private constructor (
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

    companion object Builder : IImageDetectionWindowFactory2 {
        override fun buildWindow(initialSettings: IWindowSettings): ImageDetectionSingleWindow = ImageDetectionSingleWindow(
            initialSettings, emptySet()
        )

        override fun buildWindow(
            initialSettings: IWindowSettings,
            supergroup: Set<String>
        ) = ImageDetectionSingleWindow(
            initialSettings, emptySet()
        )
    }
}