package com.example.driverchecker.machinelearning.repositories

import com.example.driverchecker.machinelearning.data.*

interface IMachineLearningFactory<I, O : IMachineLearningOutputStatsOld, FR : IMachineLearningFinalResultOld>
    : IMachineLearningRepository<I, O, FR> {
    fun use (modelName: String, modelInit: Map<String, Any?>) : Boolean
}