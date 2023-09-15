package com.example.driverchecker.machinelearning.helpers.windows

import com.example.driverchecker.machinelearning.data.IMachineLearningFinalResult
import com.example.driverchecker.machinelearning.data.IMachineLearningFinalResultStats
import com.example.driverchecker.machinelearning.data.IMachineLearningOutputStats
import com.example.driverchecker.machinelearning.data.MachineLearningFullFinalResult

open class MachineLearningWindow<E : IMachineLearningOutputStats> (size: Int = 3, threshold: Float = 0.15f) :
    AMachineLearningWindow<E> (size, threshold) {
    override fun getFinalResults() : IMachineLearningFinalResultStats {
        return MachineLearningFullFinalResult(confidence)
    }
}