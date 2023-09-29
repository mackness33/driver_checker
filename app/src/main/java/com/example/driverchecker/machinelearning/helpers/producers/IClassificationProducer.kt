package com.example.driverchecker.machinelearning.helpers.producers

interface IClassificationProducer<S> : IModelStateProducer<S> {
    suspend fun classificationReady(isReady: Boolean)
}