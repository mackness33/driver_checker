package com.example.driverchecker.machinelearning.repositories

import com.example.driverchecker.machinelearning.data.IOldClassificationFinalResult
import com.example.driverchecker.machinelearning.data.IClassificationOutputStats

interface IClassificationRepository<in I, out O : IClassificationOutputStats<S>, FR : IOldClassificationFinalResult<S>, S> :
    IMachineLearningRepository<I, O, FR> {
    fun loadClassifications(json: String?) : Boolean
}