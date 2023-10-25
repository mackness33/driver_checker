package com.example.driverchecker.machinelearning.windows.singles

import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.machinelearning.windows.helpers.ImageDetectionTag
import com.example.driverchecker.machinelearning.windows.helpers.SingleGroupTag

open class SingleGroupWindow : ImageDetectionSingleWindow {
    constructor (
        initialSettings: IMachineLearningSingleWindowSettings,
        initialClassificationSettings: IClassificationSingleWindowSettings<String>
    ) : super (initialSettings, initialClassificationSettings, SingleGroupTag)

    protected constructor (
        initialSettings: IMachineLearningSingleWindowSettings,
        initialClassificationSettings: IClassificationSingleWindowSettings<String>,
        internalTag: ImageDetectionTag
    ) : super (initialSettings, initialClassificationSettings, internalTag)

    override fun preUpdate (element: IImageDetectionOutput<String>) : Boolean {
        if (element.stats.groups.size > 1) {
            return false
        }

        return super.preUpdate(element)
    }
}