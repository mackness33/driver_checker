package com.example.driverchecker.machinelearning.windows.singles

import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.machinelearning.windows.helpers.IWindowTag
import com.example.driverchecker.machinelearning.windows.helpers.ImageDetectionTag
import com.example.driverchecker.machinelearning.windows.helpers.SingleGroupOffsetTag
import com.example.driverchecker.machinelearning.windows.helpers.SingleGroupTag

open class OffsetSingleGroupWindow : SingleGroupWindow {
    constructor (initialSettings: IClassificationSingleWindowSettings<String>) :
            super (initialSettings, SingleGroupOffsetTag)

    protected constructor (initialSettings: IClassificationSingleWindowSettings<String>, internalTag: ImageDetectionTag) :
            super (initialSettings, internalTag)

    protected val offset: Int = 0

    override fun isSatisfied() : Boolean {
        return totalWindows >= offset && super.isSatisfied()
    }
}