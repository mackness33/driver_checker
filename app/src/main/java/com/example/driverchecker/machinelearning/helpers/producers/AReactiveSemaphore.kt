package com.example.driverchecker.machinelearning.helpers.producers

import kotlinx.coroutines.sync.Mutex

abstract class AReactiveSemaphore<S> : IReactiveSemaphore<S> {
    protected val mReadyMap : MutableMap<S, Boolean> = mutableMapOf()
    protected val mutex: Mutex = Mutex()
    override val readyMap : Map<S, Boolean>
        get () = mReadyMap

    override fun initialize (semaphores: Set<S>) {
        mReadyMap.putAll(semaphores.associateWith { false })
    }

    protected abstract suspend fun action ()

    override suspend fun update (key: S, value: Boolean, triggerAction: Boolean) {
        mutex.lock()

        mReadyMap[key] = value
        if (triggerAction) action()

        mutex.unlock()
    }
}