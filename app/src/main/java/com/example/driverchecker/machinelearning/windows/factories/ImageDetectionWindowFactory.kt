package com.example.driverchecker.machinelearning.windows.factories

import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.machinelearning.windows.helpers.MultipleGroupOffsetTag
import com.example.driverchecker.machinelearning.windows.helpers.SingleGroupOffsetTag
import com.example.driverchecker.machinelearning.windows.helpers.MultipleGroupTag
import com.example.driverchecker.machinelearning.windows.helpers.SingleGroupTag
import com.example.driverchecker.machinelearning.windows.singles.*

class ImageDetectionWindowFactory : IClassificationWindowFactory<
        IImageDetectionOutput<String>,
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
            SingleGroupOffsetTag -> settings to OffsetSingleGroupWindow(settings)
            MultipleGroupTag -> settings to MultipleGroupWindow(settings)
            SingleGroupTag -> settings to SingleGroupWindow(settings)
            MultipleGroupOffsetTag -> settings to OffsetMultipleGroupWindow(settings)
            null -> TODO()
        }
    }
}