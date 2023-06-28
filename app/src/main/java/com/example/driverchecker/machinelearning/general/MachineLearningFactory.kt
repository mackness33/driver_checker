package com.example.driverchecker.machinelearning.general

import com.example.driverchecker.machinelearning.data.*

interface MachineLearningFactory<Data, Result : WithConfidence>
    : IMachineLearningRepository<Data, Result> {
    fun use (modelName: String, modelInit: Map<String, Any?>) : Boolean
}