package com.example.driverchecker.machinelearning.helpers.windows.factories

import com.example.driverchecker.machinelearning.data.IWindowSettingsOld
import com.example.driverchecker.machinelearning.data.SingleWindowSettings
import com.example.driverchecker.machinelearning.helpers.windows.singles.*

class ImageDetectionWindowFactory {
    fun createWindow (initialSettings: IWindowSettingsOld,
                      supergroup: Set<String>
    ): ImageDetectionSingleWindow? {
        return when (initialSettings.type) {
            "BasicImageDetectionWindow" -> ImageDetectionSingleWindow(
                SingleWindowSettings(
                    initialSettings.windowFrames,
                    initialSettings.windowThreshold,
                    supergroup
                )
            )
            // TODO: throw NonExistentWindowFactoryException
            else -> null
        }
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
}