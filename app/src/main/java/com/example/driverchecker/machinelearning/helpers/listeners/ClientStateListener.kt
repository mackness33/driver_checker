package com.example.driverchecker.machinelearning.helpers.listeners

import com.example.driverchecker.machinelearning.data.*

interface ClientStateListener : IGenericListener<ClientStateInterface> {
    // handler of mlRepo in ready
    suspend fun onLiveEvaluationReady ()

    // handler of mlRepo in start
    suspend fun onLiveEvaluationStart(state: ClientState.Start<*>)

    // handler of mlRepo on end
    fun onLiveEvaluationStop (state: ClientState.Stop)

    fun onLiveEvaluationUpdateSettings (state: ClientState.UpdateSettings)
}