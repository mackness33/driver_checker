package com.example.driverchecker.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex

open class AtomicObject<T> (initialValue: T?) {
    protected val mutex = Mutex(false) // private mutable state flow
    protected var value: T?

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