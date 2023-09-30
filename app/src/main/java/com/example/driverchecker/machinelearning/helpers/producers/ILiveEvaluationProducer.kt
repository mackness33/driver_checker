package com.example.driverchecker.machinelearning.helpers.producers

interface ILiveEvaluationProducer<S> : IProducer<S> {
    suspend fun emitReady(isReady: Boolean)
    suspend fun emitStart()
    suspend fun emitLoading()
    suspend fun emitErrorEnd(cause: Throwable)
    suspend fun emitSuccessEnd()

    fun tryEmitReady(isReady: Boolean) : Boolean
}