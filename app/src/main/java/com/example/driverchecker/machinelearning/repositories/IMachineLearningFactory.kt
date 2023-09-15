package com.example.driverchecker.machinelearning.repositories

import com.example.driverchecker.machinelearning.data.*

interface IMachineLearningFactory<I, O : IMachineLearningOutputMetrics, FR : IMachineLearningFinalResult>
    : IMachineLearningRepository<I, O, FR> {
    fun use (modelName: String, modelInit: Map<String, Any?>) : Boolean
}