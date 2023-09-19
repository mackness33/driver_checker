package com.example.driverchecker.machinelearning.helpers.listeners

import com.example.driverchecker.machinelearning.data.LiveEvaluationState
import com.example.driverchecker.machinelearning.data.LiveEvaluationStateInterface
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharedFlow

abstract class AMachineLearningListener : AGenericListener<LiveEvaluationStateInterface>, MachineLearningListener {
    constructor () : super()

    constructor (scope: CoroutineScope, inputFlow: SharedFlow<LiveEvaluationStateInterface>, mode: IGenericMode = GenericMode.None) :
            super(scope, inputFlow, mode)

    override suspend fun collectStates (state: LiveEvaluationStateInterface) {
        super.collectStates(state)

        when (state) {
            is LiveEvaluationState.Ready -> onLiveEvaluationReady(state)
            is LiveEvaluationState.Start -> onLiveEvaluationStart()
            is LiveEvaluationState.Loading -> onLiveEvaluationLoading(state)
            is LiveEvaluationState.OldEnd -> onLiveEvaluationOldEnd(state)
            is LiveEvaluationState.End -> onLiveEvaluationEnd(state)
            else -> {}
        }
    }
}