package com.example.driverchecker.machinelearning.helpers.producers

interface IReactiveSemaphore<S> {
    val readyMap : Map<S, Boolean>
    fun initialize (semaphores: Set<S>)
    suspend fun update (key: S, value: Boolean, triggerAction: Boolean)
}