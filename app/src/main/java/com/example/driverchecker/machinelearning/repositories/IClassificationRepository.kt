package com.example.driverchecker.machinelearning.repositories

import com.example.driverchecker.machinelearning.data.IClassificationFinalResult
import com.example.driverchecker.machinelearning.data.IClassificationFinalResultOld
import com.example.driverchecker.machinelearning.data.IClassificationOutput
import com.example.driverchecker.machinelearning.data.IClassificationOutputStatsOld

interface IClassificationRepository<in I, out O : IClassificationOutput<S>, FR : IClassificationFinalResult<S>, S> :
    IMachineLearningRepository<I, O, FR> {
    fun loadClassifications(json: String?) : Boolean
}