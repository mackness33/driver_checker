package com.example.driverchecker.machinelearning.helpers.windows.singles

import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.machinelearning.helpers.windows.helpers.IWindowTag
import com.example.driverchecker.machinelearning.helpers.windows.helpers.SingleGroupImageDetectionTag

open class ImageDetectionSingleWindow
    : AClassificationSingleWindow<IClassificationOutputStats<String>, String> {

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