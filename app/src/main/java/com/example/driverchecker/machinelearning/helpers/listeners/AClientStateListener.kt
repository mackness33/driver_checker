package com.example.driverchecker.machinelearning.helpers.listeners

import com.example.driverchecker.machinelearning.data.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharedFlow

abstract class AClientStateListener : ClientStateListener, AGenericListener<ClientStateInterface> {
    constructor () : super()

    constructor (scope: CoroutineScope, inputFlow: SharedFlow<ClientStateInterface>, mode: IGenericMode = GenericMode.None) :
            super(scope, inputFlow, mode)

    override suspend fun collectStates (state: ClientStateInterface) {
        super.collectStates(state)
        when (state) {
            is ClientState.Ready -> onLiveEvaluationReady()
            is ClientState.Start<*> -> onLiveEvaluationStart(state)
            is ClientState.Stop -> onLiveEvaluationStop(state)
            is ClientState.UpdateSettings -> onLiveEvaluationUpdateSettings(state)
        }
    }
}