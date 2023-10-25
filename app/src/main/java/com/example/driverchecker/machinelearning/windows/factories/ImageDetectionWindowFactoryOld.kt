package com.example.driverchecker.machinelearning.windows.factories

import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.machinelearning.windows.helpers.MultipleGroupOffsetTag
import com.example.driverchecker.machinelearning.windows.helpers.SingleGroupOffsetTag
import com.example.driverchecker.machinelearning.windows.helpers.MultipleGroupTag
import com.example.driverchecker.machinelearning.windows.helpers.SingleGroupTag
import com.example.driverchecker.machinelearning.windows.singles.*

class ImageDetectionWindowFactoryOld :
    IOffsetWindowFactory<
        IImageDetectionOutput<String>, IOffsetSingleWindowSettings,
        ImageDetectionSingleWindow, String>,
    IClassificationWindowFactory <
            IImageDetectionOutput<String>, IOffsetSingleWindowSettings,
            ImageDetectionSingleWindow, String>
{
    override fun createWindow (initialSettings: IOffsetSingleWindowSettings): ImageDetectionSingleWindow {
        return makeWindow(initialSettings, ClassificationSingleWindowSettings()).second
    }
    override fun createMapOfWindow (collectionOfSettings: Set<IOffsetSingleWindowSettings>):
            Map<IOffsetSingleWindowSettings, ImageDetectionSingleWindow> {
        return collectionOfSettings.map { settings -> makeWindow(settings,ClassificationSingleWindowSettings()) }.toMap()
    }

    override fun createWindow (
        initialSettings: IOffsetSingleWindowSettings,
        initialClassificationSettings: IClassificationSingleWindowSettings<String>
    ): ImageDetectionSingleWindow {
        return makeWindow(initialSettings, initialClassificationSettings).second
    }

    override fun createMapOfWindow (
        collectionOfSettings: Set<IOffsetSingleWindowSettings>,
        initialClassificationSettings: IClassificationSingleWindowSettings<String>
    ): Map<IOffsetSingleWindowSettings, ImageDetectionSingleWindow> {
        return collectionOfSettings.map { settings -> makeWindow(settings, initialClassificationSettings) }.toMap()
    }

    private fun makeWindow (
        settings: IOffsetSingleWindowSettings,
        classificationSettings: IClassificationSingleWindowSettings<String>
    ) :
            Pair<IOffsetSingleWindowSettings, ImageDetectionSingleWindow> {
        // TODO: make the single window return it's own settings
        return when (settings.tag) {
            SingleGroupOffsetTag -> settings to OffsetSingleGroupWindow(settings, classificationSettings)
            MultipleGroupTag -> settings to MultipleGroupWindow(settings, classificationSettings)
            SingleGroupTag -> settings to SingleGroupWindow(settings, classificationSettings)
            MultipleGroupOffsetTag -> settings to OffsetMultipleGroupWindow(settings, classificationSettings)
            null -> TODO()
        }
    }
}