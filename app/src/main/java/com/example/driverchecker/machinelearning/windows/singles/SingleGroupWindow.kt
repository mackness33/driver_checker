package com.example.driverchecker.machinelearning.windows.singles

import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.machinelearning.windows.helpers.IWindowTag
import com.example.driverchecker.machinelearning.windows.helpers.ImageDetectionTag
import com.example.driverchecker.machinelearning.windows.helpers.SingleGroupTag

open class SingleGroupWindow : ImageDetectionSingleWindow {
    constructor (initialSettings: IClassificationSingleWindowSettings<String>) :
            super (initialSettings, SingleGroupTag)

    protected constructor (initialSettings: IClassificationSingleWindowSettings<String>, internalTag: ImageDetectionTag) :
            super (initialSettings, internalTag)

    override fun preUpdate (element: IImageDetectionOutput<String>) : Boolean {
        if (element.stats.groups.size > 1) {
            return false
        }

        return super.preUpdate(element)
    }
}