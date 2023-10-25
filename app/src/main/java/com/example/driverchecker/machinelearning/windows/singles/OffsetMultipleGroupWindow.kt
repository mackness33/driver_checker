package com.example.driverchecker.machinelearning.windows.singles

import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.machinelearning.windows.helpers.*

open class OffsetMultipleGroupWindow : MultipleGroupWindow {
    constructor (initialSettings: IClassificationSingleWindowSettings<String>) :
            super (initialSettings, MultipleGroupOffsetTag)

    protected constructor (initialSettings: IClassificationSingleWindowSettings<String>, internalTag: ImageDetectionTag) :
            super (initialSettings, internalTag)

    protected val offset: Int = 0

    override fun isSatisfied() : Boolean {
        return totalWindows >= offset && super.isSatisfied()
    }
}