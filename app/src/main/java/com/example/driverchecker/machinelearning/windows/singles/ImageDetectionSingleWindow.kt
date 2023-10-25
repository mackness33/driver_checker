package com.example.driverchecker.machinelearning.windows.singles

import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.machinelearning.windows.helpers.ImageDetectionTag
import com.example.driverchecker.machinelearning.windows.helpers.MultipleGroupTag

open class ImageDetectionSingleWindow
    : AClassificationSingleWindow<IImageDetectionOutput<String>, String> {

    constructor (
        initialSettings: IMachineLearningSingleWindowSettings,
        initialClassificationSettings: IClassificationSingleWindowSettings<String>
    ) : super (initialSettings, initialClassificationSettings, MultipleGroupTag)

    protected constructor (
        initialSettings: IMachineLearningSingleWindowSettings,
        initialClassificationSettings: IClassificationSingleWindowSettings<String>,
        internalTag: ImageDetectionTag
    ) : super (initialSettings, initialClassificationSettings, internalTag)

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
            null
        )
    }

    override fun getFinalResults(): IClassificationFinalResult<String> {
        return ClassificationFinalResult(
            ClassificationFinalResultStats(confidence, group!!),
            ClassificationFinalResultMetrics(getData())
        )
    }
}