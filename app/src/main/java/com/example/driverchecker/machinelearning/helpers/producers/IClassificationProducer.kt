package com.example.driverchecker.machinelearning.helpers.producers

interface IClassificationProducer<S> : IModelStateProducer<S> {
    fun classificationReady(isReady: Boolean)
}