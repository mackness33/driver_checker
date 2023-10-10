package com.example.driverchecker.machinelearning.helpers.listeners

import com.example.driverchecker.machinelearning.data.*

interface ClassificationListener<S> : MachineLearningListener, IGenericListener<LiveEvaluationStateInterface> {
    // handler of mlRepo in start (as a classification)
    suspend fun onLiveClassificationStart (state: LiveClassificationState.Start<S>)

    suspend fun onLiveClassificationLoading (state: LiveClassificationState.Loading<S>)

    suspend fun onLiveClassificationEnd (state: LiveClassificationState.End<S>)
}