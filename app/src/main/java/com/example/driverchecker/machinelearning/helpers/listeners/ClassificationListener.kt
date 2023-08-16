package com.example.driverchecker.machinelearning.helpers.listeners

import com.example.driverchecker.machinelearning.data.*

interface ClassificationListener<S> : MachineLearningListener, IGenericListener<LiveEvaluationStateInterface> {
    override suspend fun collectStates (state: LiveEvaluationStateInterface) {
        super<IGenericListener>.collectStates(state)

        when (state) {
            is LiveClassificationState.Start -> onLiveClassificationStart(state)
            is LiveClassificationState.End<*> -> onLiveClassificationEnd(state as LiveClassificationState.End<S>)
            else -> super<MachineLearningListener>.collectStates(state)
        }
    }

    // handler of mlRepo in start (as a classification)
    fun onLiveClassificationStart (state: LiveClassificationState.Start)

    fun onLiveClassificationEnd (state: LiveClassificationState.End<S>)
}