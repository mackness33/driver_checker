package com.example.driverchecker.machinelearning.helpers.listeners

import com.example.driverchecker.machinelearning.data.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

interface ClientStateListener {
    fun listen (scope: CoroutineScope, clientFlow: SharedFlow<ClientStateInterface>?) : Job {
        return scope.launch(Dispatchers.Default) {
            clientFlow?.collect {state -> collectClientStates(state)}
        }
    }

    fun destroy ()

    suspend fun collectClientStates (state: ClientStateInterface) {
        when (state) {
            is ClientState.Ready -> onLiveEvaluationReady()
            is ClientState.Start<*> -> onLiveEvaluationStart(state)
            is ClientState.Stop -> onLiveEvaluationStop(state)
        }
    }

    // handler of mlRepo in ready
    fun onLiveEvaluationReady ()

    // handler of mlRepo in start
    suspend fun onLiveEvaluationStart(state: ClientState.Start<*>)

    // handler of mlRepo on end
    fun onLiveEvaluationStop (state: ClientState.Stop)
}