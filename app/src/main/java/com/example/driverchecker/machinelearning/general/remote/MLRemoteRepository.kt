package com.example.driverchecker.machinelearning.general.remote

import com.example.driverchecker.machinelearning.data.MachineLearningArrayListOutput
import com.example.driverchecker.machinelearning.general.MLRepositoryInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

abstract class MLRemoteRepository <Data, Prediction, Result : MachineLearningArrayListOutput<Data, Prediction>> (protected open val model: MLRemoteModel<Data, Result>? = null) :
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