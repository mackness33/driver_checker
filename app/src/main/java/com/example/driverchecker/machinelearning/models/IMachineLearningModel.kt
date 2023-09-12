package com.example.driverchecker.machinelearning.models

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface IMachineLearningModel <in Data, out Result> {
    fun processAndEvaluate(input: Data): Result?

    fun processAndEvaluatesStream (input: Flow<Data>): Flow<Result>?

    fun <ModelInit> loadModel (init: ModelInit)

    fun updateThreshold (newThreshold: Float)

    val isLoaded: StateFlow<Boolean>

    val threshold: Float
}