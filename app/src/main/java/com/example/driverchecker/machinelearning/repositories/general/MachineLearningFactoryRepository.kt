package com.example.driverchecker.machinelearning.repositories.general

import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.machinelearning.repositories.IMachineLearningFactory

abstract class MachineLearningFactoryRepository<Data, Result : WithConfidence>
    : MachineLearningRepository<Data, Result>, IMachineLearningFactory<Data, Result> {

    constructor() : super(null)

    constructor(modelName: String, modelInit: Map<String, Any?>) : super(null){
        initUseRepo(modelName, modelInit)
    }

    protected fun initUseRepo (modelName: String, modelInit: Map<String, Any?>) = use (modelName, modelInit)
}