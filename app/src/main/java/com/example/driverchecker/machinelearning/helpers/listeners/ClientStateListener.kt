package com.example.driverchecker.machinelearning.helpers.listeners

import com.example.driverchecker.machinelearning.data.*

interface ClientStateListener : IGenericListener<ClientStateInterface> {
    // handler of mlRepo in ready
    suspend fun onClientReady ()

    // handler of mlRepo in start
    suspend fun onClientStart(state: ClientState.Start<*>)

    // handler of mlRepo on end
    fun onClientStop (state: ClientState.Stop)

    fun onClientUpdateSettings (state: ClientState.UpdateSettings)
}