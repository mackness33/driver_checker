package com.example.driverchecker.machinelearning.repositories.general

import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.machinelearning.repositories.IMachineLearningFactory

abstract class AMachineLearningFactoryRepository<Data, Result : WithConfidence>
    : AMachineLearningRepository<Data, Result>, IMachineLearningFactory<Data, Result> {

    constructor() : super()

    constructor(modelName: String, modelInit: Map<String, Any?>) : super(){
        initUseRepo(modelName, modelInit)
    }

    protected fun initUseRepo (modelName: String, modelInit: Map<String, Any?>) = use (modelName, modelInit)
}