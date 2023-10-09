package com.example.driverchecker.machinelearning.helpers.windows.factories

import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.machinelearning.helpers.windows.helpers.HomogenousOffsetImageDetectionTag
import com.example.driverchecker.machinelearning.helpers.windows.helpers.MultipleGroupImageDetectionTag
import com.example.driverchecker.machinelearning.helpers.windows.helpers.SingleGroupImageDetectionTag
import com.example.driverchecker.machinelearning.helpers.windows.singles.*

class ImageDetectionWindowFactory {
    fun createWindow (initialSettings: IWindowSettingsOld,
                      supergroup: Set<String>
    ): ImageDetectionSingleWindow? {
        // TODO: TAG IS HARDCODED
        return makeWindow(SingleWindowSettings(
            initialSettings.windowFrames,
            initialSettings.windowThreshold,
            supergroup,
            SingleGroupImageDetectionTag
        )).second
    }

    fun createMapWindow (initialSettings: IWindowSettingsOld,
                         supergroup: Set<String>
    ): Map<IWindowSettingsOld, ImageDetectionSingleWindow?> {
//        return when (initialSettings.type) {
//            "BasicImageDetectionWindow" -> ImageDetectionSingleWindow.buildWindow(initialSettings, supergroup)
//            else -> null
//        }
        TODO("Create a map of windows based on the multipleSettings in input")

        // The settings are aggregated and outputs directly from the window!
    }

    private fun makeWindow (settings: IClassificationSingleWindowSettings<String>) :
            Pair<IClassificationSingleWindowSettings<String>, ImageDetectionSingleWindow?> {
        return when (settings.tag) {
            HomogenousOffsetImageDetectionTag -> settings to null
            MultipleGroupImageDetectionTag -> settings to null
            SingleGroupImageDetectionTag -> settings to ImageDetectionSingleWindow(settings)
            null -> settings to null // TODO: Throw an exception
        }
    }
}