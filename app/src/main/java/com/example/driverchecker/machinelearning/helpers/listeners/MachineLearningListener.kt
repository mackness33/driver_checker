package com.example.driverchecker.machinelearning.helpers.listeners

import com.example.driverchecker.machinelearning.data.LiveEvaluationState
import com.example.driverchecker.machinelearning.data.LiveEvaluationStateInterface
import com.example.driverchecker.machinelearning.data.WithConfAndSuper
import com.example.driverchecker.machinelearning.data.WithConfidence
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharedFlow

interface MachineLearningListener<Data, Result : WithConfidence> {
    fun listen (scope: CoroutineScope, evaluationFlow: SharedFlow<LiveEvaluationStateInterface>?)

    fun collectLiveEvaluations (state: LiveEvaluationStateInterface) {
        when (state) {
            is LiveEvaluationState.Ready -> onLiveEvaluationReady(state)
            is LiveEvaluationState.Start -> onLiveEvaluationStart()
            is LiveEvaluationState.Loading -> onLiveEvaluationLoading(state)
            is LiveEvaluationState.End -> onLiveEvaluationEnd(state)
            else -> {}
        }
    }

    // handler of mlRepo in ready
    fun onLiveEvaluationReady (state: LiveEvaluationState.Ready)

    // handler of mlRepo in start
    fun onLiveEvaluationStart()

    // handler of mlRepo in loading
    fun onLiveEvaluationLoading (state: LiveEvaluationState.Loading)

    // handler of mlRepo on end
    fun onLiveEvaluationEnd (state: LiveEvaluationState.End)
}