package com.example.driverchecker.machinelearning.windows.multiples

import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.machinelearning.windows.factories.ImageDetectionWindowFactory
import com.example.driverchecker.machinelearning.windows.singles.ImageDetectionSingleWindow
import kotlinx.coroutines.CoroutineScope

open class ImageDetectionMultipleWindows (scope: CoroutineScope) :
    AClassificationMultipleWindows<IImageDetectionOutput<String>, String, ImageDetectionSingleWindow, IClassificationSingleWindowSettings<String>>(scope) {
    override val factory = ImageDetectionWindowFactory()

    /* MULTIPLE */
    override var currentWindows: Map<IClassificationSingleWindowSettings<String>, ImageDetectionSingleWindow> = mutableMapOf()

    override fun getFinalResults(): IClassificationFinalResult<String> {
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
        val fr = ClassificationFinalResult(
            ClassificationFinalResultStats(finalConfidence,
            finalGroupScore.maxWith { o1, o2 -> o1.value.compareTo(o2.value) }.key,),
            ClassificationFinalResultMetrics(getData().toMutableMap())
        )

        isFinalResultBuilt.complete(null)

        return fr
    }
}
