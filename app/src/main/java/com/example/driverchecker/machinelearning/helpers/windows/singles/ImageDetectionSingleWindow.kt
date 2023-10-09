package com.example.driverchecker.machinelearning.helpers.windows.singles

import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.machinelearning.helpers.windows.factories.IImageDetectionWindowFactory2
import com.example.driverchecker.machinelearning.helpers.windows.helpers.ImageDetectionTag
import com.example.driverchecker.machinelearning.helpers.windows.helpers.SingleGroupImageDetectionTag

open class ImageDetectionSingleWindow private constructor (
    initialSettings: IWindowSettings? = null,
    supergroups: Set<String>,
) : AClassificationSingleWindow<IImageDetectionFullOutput<String>, String>(initialSettings, supergroups, SingleGroupImageDetectionTag) {
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
            tag.name,
        )
    }

    override fun getFinalResults(): IClassificationFinalResult<String> {
        return ImageDetectionFinalResult(confidence, group!!, mapOf(getData()), 0.0f)
    }

    // TODO: This static object is not useful. You can build the object however you want to without this.
    // The point is to have to different Interface for each window to create the Immutable and Mutable version
    // A window is consider Immutable the moment that it can't change it's settings. Otherwise is considered Mutable
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