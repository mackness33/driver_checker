package com.example.driverchecker.machinelearning.windows.multiples

import android.util.Log
import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.machinelearning.windows.factories.ImageDetectionWindowFactoryOld
import com.example.driverchecker.machinelearning.windows.singles.ImageDetectionSingleWindow
import kotlinx.coroutines.CoroutineScope

open class ImageDetectionMultipleWindows (scope: CoroutineScope) :
    AClassificationMultipleWindows<IImageDetectionOutput<String>, String, ImageDetectionSingleWindow, IOffsetSingleWindowSettings>(scope) {
    override val factory = ImageDetectionWindowFactoryOld()

    /* MULTIPLE */
    override var currentWindows: Map<IOffsetSingleWindowSettings, ImageDetectionSingleWindow> = mutableMapOf()

    override fun getFinalResults(): IClassificationFinalResult<String> {
        finalConfidence /= currentWindows.size
//        Log.d("TEST", finalGroupsCounter.size.toString())
//        Log.d("TEST", finalGroupsCounter.toString())
        val fr = ClassificationFinalResult(
            ClassificationFinalResultStats(
                finalConfidence,
                finalGroupsCounter.maxWith { o1, o2 -> o1.value.compareTo(o2.value) }.key),
            ClassificationFinalResultMetrics(getData().toMutableMap())
        )

        isFinalResultBuilt.complete(null)

        return fr
    }
}
