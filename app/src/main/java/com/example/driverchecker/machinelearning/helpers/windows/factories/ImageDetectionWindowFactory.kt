package com.example.driverchecker.machinelearning.helpers.windows.factories

import android.util.Log
import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.machinelearning.helpers.windows.helpers.HomogenousOffsetImageDetectionTag
import com.example.driverchecker.machinelearning.helpers.windows.helpers.MultipleGroupImageDetectionTag
import com.example.driverchecker.machinelearning.helpers.windows.helpers.SingleGroupImageDetectionTag
import com.example.driverchecker.machinelearning.helpers.windows.singles.*

class ImageDetectionWindowFactory {
    fun createWindow (initialSettings: IClassificationSingleWindowSettings<String>): ImageDetectionSingleWindow? {
        return makeWindow(initialSettings).second
    }

    fun createMapWindow (settings: IClassificationMultipleWindowSettings<String>):
            Map<IClassificationSingleWindowSettings<String>, ImageDetectionSingleWindow> {
        val windows: MutableMap<IClassificationSingleWindowSettings<String>, ImageDetectionSingleWindow> = mutableMapOf()
        // TODO: The tag is hardcoded
        settings.tags?.forEach { type ->
            settings.sizes.forEach { frames ->
                settings.thresholds.forEach { threshold ->
                    val windowSettings = SingleWindowSettings(
                        frames,
                        threshold,
                        settings.groups,
                        SingleGroupImageDetectionTag
                    )
                    val result = makeWindow(windowSettings)
                    if (result.second != null)
                        windows.putIfAbsent(result.first, result.second!!)
                }
            }
        }

        return emptyMap()
    }

    fun createMapWindow (collectionOfSettings: Set<IClassificationSingleWindowSettings<String>>):
            Map<IClassificationSingleWindowSettings<String>, ImageDetectionSingleWindow?> {
        return collectionOfSettings.map { settings -> makeWindow(settings) }.toMap()
    }

    private fun makeWindow (settings: IClassificationSingleWindowSettings<String>) :
            Pair<IClassificationSingleWindowSettings<String>, ImageDetectionSingleWindow?> {
        // TODO: make the single window return it's own settings
        return when (settings.tag) {
            HomogenousOffsetImageDetectionTag -> settings to null
            MultipleGroupImageDetectionTag -> settings to null
            SingleGroupImageDetectionTag -> settings to ImageDetectionSingleWindow(settings)
            null -> settings to null // TODO: Throw an exception
        }
    }
}