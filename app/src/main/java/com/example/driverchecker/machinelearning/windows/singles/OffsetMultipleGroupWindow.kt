package com.example.driverchecker.machinelearning.windows.singles

import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.machinelearning.windows.helpers.*

open class OffsetMultipleGroupWindow : MultipleGroupWindow {
    constructor (
        initialSettings: IOffsetSingleWindowSettings,
        initialClassificationSettings: IClassificationSingleWindowSettings<String>
    ) : super (initialSettings, initialClassificationSettings, MultipleGroupOffsetTag) {
        offset = initialSettings.offset
    }

    protected constructor (
        initialSettings: IOffsetSingleWindowSettings,
        initialClassificationSettings: IClassificationSingleWindowSettings<String>,
        internalTag: ImageDetectionTag
    ) : super (initialSettings, initialClassificationSettings, internalTag) {
        offset = initialSettings.offset
    }

    protected val offset: Int

    override fun isSatisfied() : Boolean {
        return totalWindows >= offset && super.isSatisfied()
    }

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
            offset
        )
    }
}