package com.example.driverchecker.machinelearning.helpers.listeners

import com.example.driverchecker.machinelearning.data.LiveEvaluationState
import com.example.driverchecker.machinelearning.data.LiveEvaluationStateInterface

interface MachineLearningListener : IGenericListener<LiveEvaluationStateInterface> {

    // handler of mlRepo in ready
    suspend fun onLiveEvaluationReady (state: LiveEvaluationState.Ready)

    // handler of mlRepo in start
    suspend fun onLiveEvaluationStart()

    // handler of mlRepo in loading
    suspend fun onLiveEvaluationLoading (state: LiveEvaluationState.Loading)

    // handler of mlRepo on end
    suspend fun onLiveEvaluationOldEnd (state: LiveEvaluationState.OldEnd)
    suspend fun onLiveEvaluationEnd (state: LiveEvaluationState.End)
}