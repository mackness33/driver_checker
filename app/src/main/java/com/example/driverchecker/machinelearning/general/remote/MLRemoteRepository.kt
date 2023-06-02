package com.example.driverchecker.machinelearning.general.remote

import com.example.driverchecker.machinelearning.general.MLModelInterface
import com.example.driverchecker.machinelearning.general.MLRepositoryInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

abstract class MLRemoteRepository <Data, Result> (protected open val model: MLRemoteModel<Data, Result>? = null) :
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