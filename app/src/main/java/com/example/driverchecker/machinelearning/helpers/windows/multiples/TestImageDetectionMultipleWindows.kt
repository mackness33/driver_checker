package com.example.driverchecker.machinelearning.helpers.windows.multiples

import android.util.Log
import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.machinelearning.helpers.windows.factories.ImageDetectionWindowFactory
import com.example.driverchecker.machinelearning.helpers.windows.singles.ImageDetectionSingleWindow
import kotlinx.coroutines.CoroutineScope

open class TestImageDetectionMultipleWindows (scope: CoroutineScope) :
    AClassificationMultipleWindows<IImageDetectionFullOutput<String>, String, ImageDetectionSingleWindow> (scope) {
//    override val builderList: List<>
    val factory = ImageDetectionWindowFactory()

    /* MULTIPLE */
    // TODO: improve windows management
    override var availableWindows: MutableMap<IWindowSettings, ImageDetectionSingleWindow> = mutableMapOf()
    override var selectedWindows: MutableSet<ImageDetectionSingleWindow> = mutableSetOf()

    /*  WINDOWS  */
    override fun initialize(availableSettings: ISettings) {
        settings = availableSettings
        isFinalResultBuilt.complete(null)

        try {
            val mAvailableWindows: MutableMap<IWindowSettings, ImageDetectionSingleWindow> = mutableMapOf()
            settings.multipleTypes.forEach { type ->
                settings.multipleWindowsFrames.forEach { frames ->
                    settings.multipleWindowsThresholds.forEach { threshold ->
                        val tempSettings: IWindowSettings = WindowSettings(frames, threshold, type)
                        availableWindows.putIfAbsent(
                            tempSettings,
                            factory.createWindow(tempSettings, groups)!!
                        )
                    }
                }
            }
        } catch (e: Throwable) {
            Log.e("WindowMutableSet", e.message.toString(), e)
        }
    }

    override fun getFinalResults(): IClassificationFinalResult<String> {
        val finalGroupScore = groups.associateWith { 0 }.toMutableMap()
        var finalGroupWindow: String
        var finalConfidence: Float = 0.0f
        selectedWindows.forEach { window ->
            // TODO: group must change
            finalGroupWindow = "Change"
            finalConfidence += window.confidence
            finalGroupScore[finalGroupWindow] = (finalGroupScore[finalGroupWindow] ?: 0) + 1
        }

        finalConfidence /= selectedWindows.size
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
