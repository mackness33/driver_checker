package com.example.driverchecker.machinelearning.helpers.producers

interface IModelStateProducer<S> : IProducer<S> {
    suspend fun modelReady(isReady: Boolean)
}