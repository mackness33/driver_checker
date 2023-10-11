package com.example.driverchecker.machinelearning.helpers.windows.factories

import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.machinelearning.helpers.windows.helpers.HomogenousOffsetImageDetectionTag
import com.example.driverchecker.machinelearning.helpers.windows.helpers.MultipleGroupImageDetectionTag
import com.example.driverchecker.machinelearning.helpers.windows.helpers.SingleGroupImageDetectionTag
import com.example.driverchecker.machinelearning.helpers.windows.singles.*

class ImageDetectionWindowFactory : IClassificationWindowFactory<
        IImageDetectionFullOutputOld<String>,
        IClassificationSingleWindowSettings<String>,
        ImageDetectionSingleWindow,
        String> {
    override fun createWindow (initialSettings: IClassificationSingleWindowSettings<String>): ImageDetectionSingleWindow {
        return makeWindow(initialSettings).second
    }
    override fun createMapOfWindow (collectionOfSettings: Set<IClassificationSingleWindowSettings<String>>):
            Map<IClassificationSingleWindowSettings<String>, ImageDetectionSingleWindow> {
        return collectionOfSettings.map { settings -> makeWindow(settings) }.toMap()
    }

    private fun makeWindow (settings: IClassificationSingleWindowSettings<String>) :
            Pair<IClassificationSingleWindowSettings<String>, ImageDetectionSingleWindow> {
        // TODO: make the single window return it's own settings
        return when (settings.tag) {
            HomogenousOffsetImageDetectionTag -> TODO("Throw an exception")
            MultipleGroupImageDetectionTag -> TODO("Throw an exception")
            SingleGroupImageDetectionTag -> settings to ImageDetectionSingleWindow(settings)
            null -> TODO("Throw an exception")
        }
    }
}