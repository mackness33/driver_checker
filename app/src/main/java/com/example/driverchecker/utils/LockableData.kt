package com.example.driverchecker.utils

import kotlinx.coroutines.sync.Mutex

open class LockableData<T> (initialValue: T) : AtomicObservableData<T>, StatefulData<T> (initialValue) {
    protected val mutex = Mutex(false) // private mutable state flow

    override fun isLocked() : Boolean = mutex.isLocked

    override fun tryUpdate(next: T) : Boolean {
        if (mutex.tryLock()) {
            val isApplied = tryApply(next)
            mutex.unlock()

            return isApplied
        }

        return false
    }

    override suspend fun update(next: T) {
        mutex.lock()
        apply(next)
        mutex.unlock()
    }
}