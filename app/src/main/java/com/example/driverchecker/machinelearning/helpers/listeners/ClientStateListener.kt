package com.example.driverchecker.machinelearning.helpers.listeners

import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.utils.AtomicValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

interface ClientStateListener : IGenericListener<ClientStateInterface> {
    override suspend fun collectClientStates (state: ClientStateInterface) {
        super.collectClientStates(state)
        when (state) {
            is ClientState.Ready -> onLiveEvaluationReady()
            is ClientState.Start<*> -> onLiveEvaluationStart(state)
            is ClientState.Stop -> onLiveEvaluationStop(state)
        }
    }

    // handler of mlRepo in ready
    suspend fun onLiveEvaluationReady ()

    // handler of mlRepo in start
    suspend fun onLiveEvaluationStart(state: ClientState.Start<*>)

    // handler of mlRepo on end
    fun onLiveEvaluationStop (state: ClientState.Stop)
}