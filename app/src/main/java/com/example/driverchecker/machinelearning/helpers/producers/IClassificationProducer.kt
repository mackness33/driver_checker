package com.example.driverchecker.machinelearning.helpers.producers

interface IClassificationProducer<S> : IProducer<S> {
    fun emitReady(isReady: Boolean)
    fun emitStart()
    fun emitLoading()
    fun emitErrorEnd(e: Throwable)
    fun emitSuccessEnd()

    fun tryEmitReady(isReady: Boolean) : Boolean
}