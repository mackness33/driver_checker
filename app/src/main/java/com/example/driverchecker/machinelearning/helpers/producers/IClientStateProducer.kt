package com.example.driverchecker.machinelearning.helpers.producers

import com.example.driverchecker.machinelearning.data.ClientStateInterface
import com.example.driverchecker.machinelearning.data.ExternalCancellationException

interface IClientStateProducer<S: ClientStateInterface> : IProducer<S> {
    suspend fun emitReady()
    suspend fun emitStart()
    suspend fun emitStop(cause: ExternalCancellationException)
}