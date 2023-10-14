package com.example.driverchecker.machinelearning.windows.singles

import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.machinelearning.windows.helpers.IWindowTag
import com.example.driverchecker.machinelearning.windows.helpers.SingleGroupImageDetectionTag

open class ImageDetectionSingleWindow
    : AClassificationSingleWindow<IImageDetectionOutput<String>, String> {

    constructor (initialSettings: IClassificationSingleWindowSettings<String>) :
            super (initialSettings, SingleGroupImageDetectionTag)

    protected constructor (initialSettings: IClassificationSingleWindowSettings<String>, internalTag: IWindowTag) :
            super (initialSettings, internalTag)

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
        return ClassificationFinalResult(
            ClassificationFinalResultStats(confidence, group!!),
            ClassificationFinalResultMetrics(getData())
        )
    }
}