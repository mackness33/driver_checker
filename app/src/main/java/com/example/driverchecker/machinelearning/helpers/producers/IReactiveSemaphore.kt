package com.example.driverchecker.machinelearning.helpers.producers

interface IReactiveSemaphore<S> : IProducer<S> {
    fun modelReady(isReady: Boolean)
}