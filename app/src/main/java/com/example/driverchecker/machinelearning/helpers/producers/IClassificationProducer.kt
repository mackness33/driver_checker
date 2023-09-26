package com.example.driverchecker.machinelearning.helpers.producers

interface IClassificationProducer<S> : IProducer<S> {
    suspend fun emitReady(isReady: Boolean)
    suspend fun emitStart()
    suspend fun emitLoading()
    suspend fun emitErrorEnd(e: Throwable)
    suspend fun emitSuccessEnd()

    fun tryEmitReady(isReady: Boolean) : Boolean
}