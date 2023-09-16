package com.example.driverchecker.utils

import kotlinx.coroutines.sync.Mutex

open class LockableData<T> (initialValue: T) : AtomicObservableData<T>, StatefulData<T> (initialValue) {
    protected val mutex = Mutex(false) // private mutable state flow

    override fun tryUpdate(next: T) {
        if (mutex.tryLock()) {
            tryApply(next)
            mutex.unlock()
        }
    }

    override suspend fun update(next: T) {
        mutex.lock()
        apply(next)
        mutex.unlock()
    }
}