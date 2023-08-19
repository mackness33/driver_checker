package com.example.driverchecker.machinelearning.helpers.listeners

import android.util.Log
import com.example.driverchecker.machinelearning.data.*

interface ClassificationListener<S> : MachineLearningListener, IGenericListener<LiveEvaluationStateInterface> {
    override suspend fun collectStates (state: LiveEvaluationStateInterface) {
        try {
            super<IGenericListener>.collectStates(state)

            when (state) {
                is LiveClassificationState.Start<*> -> onLiveClassificationStart(state as LiveClassificationState.Start<S>)
                is LiveClassificationState.Loading<*> -> onLiveClassificationLoading(state as LiveClassificationState.Loading<S>)
                is LiveClassificationState.End<*> -> onLiveClassificationEnd(state as LiveClassificationState.End<S>)
                else -> super<MachineLearningListener>.collectStates(state)
            }
        } catch (e : Throwable) {
            Log.d("ClassificationListener", "Bad cast to Start<S> or End<S>", e)
        }
    }

    // handler of mlRepo in start (as a classification)
    suspend fun onLiveClassificationStart (state: LiveClassificationState.Start<S>)

    suspend fun onLiveClassificationLoading (state: LiveClassificationState.Loading<S>)

    suspend fun onLiveClassificationEnd (state: LiveClassificationState.End<S>)
}