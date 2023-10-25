package com.example.driverchecker.machinelearning.windows.singles

import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.machinelearning.windows.helpers.*

open class OffsetSingleGroupWindow : SingleGroupWindow {
    constructor (
        initialSettings: IOffsetSingleWindowSettings,
        initialClassificationSettings: IClassificationSingleWindowSettings<String>
    ) : super (initialSettings, initialClassificationSettings, SingleGroupOffsetTag) {
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
}