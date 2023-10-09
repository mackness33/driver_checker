package com.example.driverchecker.machinelearning.helpers.windows.singles

import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.machinelearning.helpers.windows.ImageDetectionWindow
import kotlin.time.ExperimentalTime
import kotlin.time.TimeSource

@OptIn(ExperimentalTime::class)
open class BasicImageDetectionWindowOld (
    size: Int = 3,
    threshold: Float = 0.15f,
    supergroups: Set<String>,
    newStart: TimeSource.Monotonic.ValueTimeMark? = null
) : ImageDetectionWindow(size, threshold, supergroups = supergroups, newStart = newStart) {
    override val supergroup: String
        get() = supergroupCounter.maxWith { o1, o2 -> o1.value.compareTo(o2.value) }.key

    override fun getMetrics(): IWindowBasicData {
        return WindowBasicData(this)
    }

    override fun update () {
        print("")
        super.update()
    }

    override val type: String
        get() = TODO("Not yet implemented")

//    companion object Factory : IImageDetectionWindowFactory {
//        override fun buildClassificationWindow(
//            frames: Int,
//            threshold: Float,
//            groups: Set<String>
//        ): IClassificationWindow<IClassificationOutputStats<String>, String> {
//            return ImageDetectionWindow(frames, threshold, groups)
//        }
//
//        override fun buildMachineLearningWindow(
//            frames: Int,
//            threshold: Float
//        ): IMachineLearningWindow<IClassificationOutputStats<String>> {
//            return ImageDetectionWindow(frames, threshold, emptySet())
//        }
//    }
}