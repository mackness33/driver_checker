package com.example.driverchecker.machinelearning.windows.singles

import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.machinelearning.windows.helpers.IWindowTag
import com.example.driverchecker.machinelearning.windows.helpers.ImageDetectionTag
import com.example.driverchecker.machinelearning.windows.helpers.MultipleGroupTag
import com.example.driverchecker.machinelearning.windows.helpers.SingleGroupTag

open class MultipleGroupWindow : ImageDetectionSingleWindow {
    constructor (initialSettings: IClassificationSingleWindowSettings<String>) :
            super (initialSettings, MultipleGroupTag)

    protected constructor (initialSettings: IClassificationSingleWindowSettings<String>, internalTag: ImageDetectionTag) :
            super (initialSettings, internalTag)
}