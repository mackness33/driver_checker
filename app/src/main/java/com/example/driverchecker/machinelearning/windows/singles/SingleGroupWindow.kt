package com.example.driverchecker.machinelearning.windows.singles

import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.machinelearning.windows.helpers.IWindowTag
import com.example.driverchecker.machinelearning.windows.helpers.SingleGroupTag

open class SingleGroupWindow : ImageDetectionSingleWindow {

    constructor (initialSettings: IClassificationSingleWindowSettings<String>) :
            super (initialSettings, SingleGroupTag)

    protected constructor (initialSettings: IClassificationSingleWindowSettings<String>, internalTag: IWindowTag) :
            super (initialSettings, internalTag)

    override fun preUpdate (element: IImageDetectionOutput<String>) : Boolean {
        // TODO: The last check must be moved to the supergroup
        if (element.stats.groups.size > 1) {
            return false
        }

        return super.preUpdate(element)
    }
}