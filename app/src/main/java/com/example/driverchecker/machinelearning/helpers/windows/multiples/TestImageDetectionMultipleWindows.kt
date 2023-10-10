package com.example.driverchecker.machinelearning.helpers.windows.multiples

import android.util.Log
import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.machinelearning.helpers.windows.factories.ImageDetectionWindowFactory
import com.example.driverchecker.machinelearning.helpers.windows.helpers.SingleGroupImageDetectionTag
import com.example.driverchecker.machinelearning.helpers.windows.singles.ImageDetectionSingleWindow
import kotlinx.coroutines.CoroutineScope

open class TestImageDetectionMultipleWindows (scope: CoroutineScope) :
    AClassificationMultipleWindows<IImageDetectionFullOutput<String>, String, ImageDetectionSingleWindow, IClassificationSingleWindowSettings<String>> (scope) {
    val factory = ImageDetectionWindowFactory()

    /* MULTIPLE */
    // TODO: improve windows management
//    override var availableWindows: MutableMap<IWindowSettingsOld, ImageDetectionSingleWindow> = mutableMapOf()
//    override var selectedWindows: MutableSet<ImageDetectionSingleWindow> = mutableSetOf()
    override val currentWindows: MutableMap<IClassificationSingleWindowSettings<String>, ImageDetectionSingleWindow> = mutableMapOf()

    init {
        isFinalResultBuilt.complete(null)
    }

    fun update (newSettings: IClassificationMultipleWindowSettings<String>) {
        // get the list of settings as a set and get all the windows that are not part of the current ones
        val listOfNewSettings = newSettings.asListOfSettings().toSet()
        val newWindows = factory.createMapOfWindow(listOfNewSettings.minus(currentWindows.keys))

        // remove the windows not part of the new settings and add the one that are not there
        currentWindows.minusAssign(currentWindows.keys.minus(listOfNewSettings))
        currentWindows.plusAssign(newWindows)

        activeWindows = currentWindows.values.toSet()
    }

    /*  WINDOWS  */
    override fun initialize(availableSettings: ISettingsOld) {
//        settings = availableSettings
//        isFinalResultBuilt.complete(null)
//        val windows: MutableMap<IClassificationSingleWindowSettings<String>, ImageDetectionSingleWindow?> = mutableMapOf()
//
//        try {
//            val mAvailableWindows: MutableMap<IWindowSettingsOld, ImageDetectionSingleWindow> = mutableMapOf()
//            settings.multipleTypes.forEach { type ->
//                settings.multipleWindowsFrames.forEach { frames ->
//                    settings.multipleWindowsThresholds.forEach { threshold ->
//                        val tempSettings: IWindowSettingsOld = WindowSettingsOld(frames, threshold, type)
//                        // TODO: TAG IS HARDCODED
//                        availableWindows.putIfAbsent(
//                            tempSettings,
//                            factory.createWindow(
//                                SingleWindowSettings(
//                                    tempSettings.windowFrames,
//                                    tempSettings.windowThreshold,
//                                    groups,
//                                    SingleGroupImageDetectionTag
//                                )
//                            )!!
//                        )
//                    }
//                }
//            }
//        } catch (e: Throwable) {
//            Log.e("WindowMutableSet", e.message.toString(), e)
//        }
    }

    override fun getFinalResults(): IClassificationFinalResult<String> {
        val finalGroupScore = groups.associateWith { 0 }.toMutableMap()
        var finalGroupWindow: String
        var finalConfidence = 0.0f
//        selectedWindows.forEach { window ->
//            // TODO: group must change
//            finalGroupWindow = "Change"
//            finalConfidence += window.confidence
//            finalGroupScore[finalGroupWindow] = (finalGroupScore[finalGroupWindow] ?: 0) + 1
//        }

        currentWindows.values.forEach { window ->
            // TODO: group must change
            finalGroupWindow = "Change"
            finalConfidence += window.confidence
            finalGroupScore[finalGroupWindow] = (finalGroupScore[finalGroupWindow] ?: 0) + 1
        }


        finalConfidence /= currentWindows.size
        val fr = ClassificationFinalResult(
            finalConfidence,
            finalGroupScore.maxWith { o1, o2 -> o1.value.compareTo(o2.value) }.key,
            getData().toMutableMap(),
            settings.modelThreshold
        )

        isFinalResultBuilt.complete(null)

        return fr
    }
}
