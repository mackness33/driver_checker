package com.example.driverchecker.machinelearning.general

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface IMachineLearningModel <Data, Result> {
    fun processAndEvaluate(input: Data): Result?

    fun processAndEvaluatesStream (input: Flow<Data>): Flow<Result>?

    fun <ModelInit> loadModel (init: ModelInit)

    val isLoaded: StateFlow<Boolean>
}