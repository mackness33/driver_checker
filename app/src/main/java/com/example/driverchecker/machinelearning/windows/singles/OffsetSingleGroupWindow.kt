package com.example.driverchecker.machinelearning.windows.singles

import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.machinelearning.windows.helpers.*

open class OffsetSingleGroupWindow : SingleGroupWindow {
    constructor (
        initialSettings: IMachineLearningSingleWindowSettings,
        initialClassificationSettings: IClassificationSingleWindowSettings<String>
    ) : super (initialSettings, initialClassificationSettings, SingleGroupOffsetTag)

    protected constructor (
        initialSettings: IMachineLearningSingleWindowSettings,
        initialClassificationSettings: IClassificationSingleWindowSettings<String>,
        internalTag: ImageDetectionTag
    ) : super (initialSettings, initialClassificationSettings, internalTag)

    protected val offset: Int = 0

    override fun isSatisfied() : Boolean {
        return totalWindows >= offset && super.isSatisfied()
    }
}