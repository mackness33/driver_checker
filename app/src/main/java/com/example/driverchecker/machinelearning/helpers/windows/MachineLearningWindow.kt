package com.example.driverchecker.machinelearning.helpers.windows

import com.example.driverchecker.machinelearning.data.MachineLearningFullFinalResult
import com.example.driverchecker.machinelearning.data.WithConfidence

open class MachineLearningWindow<E : WithConfidence> (size: Int = 3, threshold: Float = 0.15f) :
    AMachineLearningWindow<E> (size, threshold) {
    override fun getFinalResults() : WithConfidence {
        return MachineLearningFullFinalResult(confidence, window)
    }
}