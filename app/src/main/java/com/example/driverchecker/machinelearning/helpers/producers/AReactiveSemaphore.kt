package com.example.driverchecker.machinelearning.helpers.producers

interface AReactiveSemaphore<S> {
    val readyMap : Map<S, Boolean>
    fun initialize (semaphores: Set<S>)
    fun update (key: S, value: Boolean, triggerAction: Boolean)
}