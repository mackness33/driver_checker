package com.example.driverchecker.machinelearning.helpers.listeners

import com.example.driverchecker.machinelearning.data.LiveEvaluationState
import com.example.driverchecker.machinelearning.data.LiveEvaluationStateInterface

interface MachineLearningListener : IGenericListener<LiveEvaluationStateInterface> {

    override suspend fun collectStates (state: LiveEvaluationStateInterface) {
        super.collectStates(state)

        when (state) {
            is LiveEvaluationState.Ready -> onLiveEvaluationReady(state)
            is LiveEvaluationState.Start -> onLiveEvaluationStart()
            is LiveEvaluationState.Loading -> onLiveEvaluationLoading(state)
            is LiveEvaluationState.End -> onLiveEvaluationEnd(state)
            else -> {}
        }
    }

    // handler of mlRepo in ready
    suspend fun onLiveEvaluationReady (state: LiveEvaluationState.Ready)

    // handler of mlRepo in start
    suspend fun onLiveEvaluationStart()

    // handler of mlRepo in loading
    suspend fun onLiveEvaluationLoading (state: LiveEvaluationState.Loading)

    // handler of mlRepo on end
    suspend fun onLiveEvaluationEnd (state: LiveEvaluationState.End)
}