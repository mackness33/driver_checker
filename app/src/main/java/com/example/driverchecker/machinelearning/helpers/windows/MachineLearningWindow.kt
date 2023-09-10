package com.example.driverchecker.machinelearning.helpers.windows

import com.example.driverchecker.machinelearning.data.MachineLearningFinalResult
import com.example.driverchecker.machinelearning.data.WithConfidence

open class MachineLearningWindow<E : WithConfidence> (override val size: Int = 3, override val threshold: Float = 0.15f) :
    AMachineLearningWindow<E> (size, threshold) {
    override fun getFinalResults() : WithConfidence {
        return MachineLearningFinalResult(confidence, window)
    }
}