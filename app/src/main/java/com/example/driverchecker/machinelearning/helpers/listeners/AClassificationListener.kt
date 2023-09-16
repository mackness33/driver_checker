package com.example.driverchecker.machinelearning.helpers.listeners

import android.util.Log
import com.example.driverchecker.machinelearning.data.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharedFlow

abstract class AClassificationListener<S> : AMachineLearningListener, ClassificationListener<S> {

    constructor () {}

    constructor (scope: CoroutineScope, inputFlow: SharedFlow<LiveEvaluationStateInterface>, mode: IGenericMode = GenericMode.None) :
            super(scope, inputFlow, mode)

    override suspend fun collectStates (state: LiveEvaluationStateInterface) {
        try {
            super.genericCollectStates(state)

            when (state) {
                is LiveClassificationState.Start<*> -> onLiveClassificationStart(state as LiveClassificationState.Start<S>)
                is LiveClassificationState.Loading<*> -> onLiveClassificationLoading(state as LiveClassificationState.Loading<S>)
                is LiveClassificationState.End<*> -> onLiveClassificationEnd(state as LiveClassificationState.End<S>)
                else -> super.collectStates(state)
            }
        } catch (e : Throwable) {
            Log.d("ClassificationListener", "Bad cast to Start<S> or End<S>", e)
        }
    }
}