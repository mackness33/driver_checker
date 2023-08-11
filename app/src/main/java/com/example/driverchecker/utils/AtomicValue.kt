package com.example.driverchecker.utils

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex

open class AtomicValue<T> (initialValue: T?) {
    protected val mutex = Mutex(false) // private mutable state flow
    var value: T?
        protected set

    init {
        value = initialValue
    }

    fun tryUpdate(nextValue: T) {
        if (mutex.tryLock()) {
            runBlocking {
                launch {
                    apply(nextValue)
                    mutex.unlock()
                }
            }
        }
    }

    open suspend fun apply (next: T) {
        value = next
    }

    suspend fun update(nextValue: T) {
        mutex.lock()
        apply(nextValue)
        mutex.unlock()
    }
}