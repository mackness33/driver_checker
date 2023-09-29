package com.example.driverchecker.machinelearning.helpers.producers

interface IModelStateProducer<S> : IProducer<S> {
    fun modelReady(isReady: Boolean)
}