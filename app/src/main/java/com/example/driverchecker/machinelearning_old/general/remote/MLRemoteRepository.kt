package com.example.driverchecker.machinelearning_old.general.remote

import com.example.driverchecker.machinelearning_old.data.MachineLearningArrayListOutputOld
import com.example.driverchecker.machinelearning_old.general.MLRepositoryInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

abstract class MLRemoteRepository <Data, Prediction, Superclass, Result : MachineLearningArrayListOutputOld<Data, Prediction, Superclass>> (protected open val model: MLRemoteModel<Data, Result>? = null) :
    MLRepositoryInterface<Data, Result> {
    override suspend fun instantClassification(input: Data): Result? {
        return withContext(Dispatchers.Default) {
            model?.processAndEvaluate(input)
        }
    }

    fun updateRemoteUrl (path: String) {
        model?.loadModel(path)
    }
}