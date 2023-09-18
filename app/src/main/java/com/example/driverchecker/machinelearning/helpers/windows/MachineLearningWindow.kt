package com.example.driverchecker.machinelearning.helpers.windows

import com.example.driverchecker.machinelearning.data.IMachineLearningFinalResultStats
import com.example.driverchecker.machinelearning.data.IMachineLearningOutputStats
import com.example.driverchecker.machinelearning.data.OldMachineLearningFullFinalResult
import kotlin.time.ExperimentalTime
import kotlin.time.TimeSource

@OptIn(ExperimentalTime::class)
open class MachineLearningWindow<E : IMachineLearningOutputStats> (
    size: Int = 3, threshold: Float = 0.15f, override val type: String,
    newStart: TimeSource.Monotonic.ValueTimeMark? = null
) :
    AMachineLearningWindow<E> (size, threshold, newStart) {
    override fun getFinalResults() : IMachineLearningFinalResultStats {
        return OldMachineLearningFullFinalResult(confidence)
    }
}