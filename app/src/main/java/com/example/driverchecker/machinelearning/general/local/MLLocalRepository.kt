package com.example.driverchecker.machinelearning.general.local

import com.example.driverchecker.machinelearning.general.MLModelInterface
import com.example.driverchecker.machinelearning.general.MLRepositoryInterface

abstract class MLLocalRepository <Data, Result> (protected open val model: MLLocalModel<Data, Result>? = null) :
    MLRepositoryInterface<Data, Result> {
    override suspend fun instantClassification(input: Data): Result? {
        return model?.processAndEvaluate (input)
    }

    override suspend fun continuousClassification(input: List<Data>): Result? {
//        TODO("Not yet implemented")
        return null
    }

    fun updateLocalModel (path: String) {
        model?.loadModel(path)
    }
}