package com.example.driverchecker.machinelearning.helpers.windows.multiples

import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.machinelearning.helpers.windows.factories.ImageDetectionWindowFactory
import com.example.driverchecker.machinelearning.helpers.windows.singles.ImageDetectionSingleWindow
import kotlinx.coroutines.CoroutineScope

open class ImageDetectionMultipleWindows (scope: CoroutineScope) :
    AClassificationMultipleWindows<IClassificationOutputStats<String>, String, ImageDetectionSingleWindow, IClassificationSingleWindowSettings<String>> (scope) {
    override val factory = ImageDetectionWindowFactory()

    /* MULTIPLE */
    override var currentWindows: Map<IClassificationSingleWindowSettings<String>, ImageDetectionSingleWindow> = mutableMapOf()

    override fun getFinalResults(): IClassificationFinalResultOld<String> {
        val finalGroupScore = groups.associateWith { 0 }.toMutableMap()
        var finalGroupWindow: String
        var finalConfidence = 0.0f

        currentWindows.values.forEach { window ->
            // TODO: group must change
            finalGroupWindow = "Change"
            finalConfidence += window.confidence
            finalGroupScore[finalGroupWindow] = (finalGroupScore[finalGroupWindow] ?: 0) + 1
        }


        finalConfidence /= currentWindows.size
        val fr = ClassificationFinalResultOld(
            finalConfidence,
            finalGroupScore.maxWith { o1, o2 -> o1.value.compareTo(o2.value) }.key,
            getData().toMutableMap(),
            0.30f
        )

        isFinalResultBuilt.complete(null)

        return fr
    }
}
