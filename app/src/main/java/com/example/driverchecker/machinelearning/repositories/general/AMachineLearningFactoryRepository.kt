package com.example.driverchecker.machinelearning.repositories.general

import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.machinelearning.repositories.IMachineLearningFactory
import kotlinx.coroutines.CoroutineScope

abstract class AMachineLearningFactoryRepository<I, O : IMachineLearningOutputStatsOld, FR : IMachineLearningFinalResult>
    : AMachineLearningRepository<I, O, FR>, IMachineLearningFactory<I, O, FR> {

    constructor(repositoryScope: CoroutineScope) : super(repositoryScope)

    constructor(modelName: String, modelInit: Map<String, Any?>, repositoryScope: CoroutineScope) : super(repositoryScope){
        initUseRepo(modelName, modelInit)
    }

    protected fun initUseRepo (modelName: String, modelInit: Map<String, Any?>) = use (modelName, modelInit)
}