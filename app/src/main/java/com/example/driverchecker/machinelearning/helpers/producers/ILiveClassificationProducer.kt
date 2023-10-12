package com.example.driverchecker.machinelearning.helpers.producers

import com.example.driverchecker.machinelearning.data.IClassificationOutput
import com.example.driverchecker.machinelearning.data.IMachineLearningOutput

interface ILiveClassificationProducer<S, G> : ILiveEvaluationProducer<S> {
    suspend fun emitLoading(output: IClassificationOutput<G>)
}