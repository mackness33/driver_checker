package com.example.driverchecker.machinelearning.general.remote

import com.example.driverchecker.machinelearning.general.MLModelInterface
import com.example.driverchecker.machinelearning.general.MLRepositoryInterface

abstract class MLRemoteRepository <Data, Result> (protected open val model: MLRemoteModel<Data, Result>? = null) :
    MLRepositoryInterface<Data, Result> {
    override suspend fun instantClassification(input: Data): Result? {
        return model?.processAndEvaluate (input)
    }

    override suspend fun continuousClassification(input: List<Data>): Result? {
        //        TODO("Not yet implemented")

        return null
    }

    fun updateRemoteUrl (path: String) {
        model?.loadModel(path)
    }
}