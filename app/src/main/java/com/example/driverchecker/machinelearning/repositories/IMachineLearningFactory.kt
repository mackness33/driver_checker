package com.example.driverchecker.machinelearning.repositories

import com.example.driverchecker.machinelearning.data.*

interface IMachineLearningFactory<Data, Result : WithConfidence>
    : IMachineLearningRepository<Data, Result> {
    fun use (modelName: String, modelInit: Map<String, Any?>) : Boolean
}