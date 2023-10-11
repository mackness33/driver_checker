package com.example.driverchecker.machinelearning.repositories

import com.example.driverchecker.machinelearning.data.IClassificationFinalResultOld
import com.example.driverchecker.machinelearning.data.IClassificationOutputStatsOld

interface IClassificationRepository<in I, out O : IClassificationOutputStatsOld<S>, FR : IClassificationFinalResultOld<S>, S> :
    IMachineLearningRepository<I, O, FR> {
    fun loadClassifications(json: String?) : Boolean
}