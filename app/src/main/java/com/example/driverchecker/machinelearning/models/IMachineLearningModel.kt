package com.example.driverchecker.machinelearning.models

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

interface IMachineLearningModel <in I, out O> {
    fun processAndEvaluate(input: I): O?

    fun processAndEvaluatesStream (input: Flow<I>): Flow<O>?

    fun <ModelInit> loadModel (init: ModelInit)

    fun updateThreshold (newThreshold: Float)

    val isLoaded: SharedFlow<Boolean>

    val threshold: Float

    val modelScope: CoroutineScope
}