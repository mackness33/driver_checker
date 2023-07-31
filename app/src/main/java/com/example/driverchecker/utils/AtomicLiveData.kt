package com.example.driverchecker.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex

class AtomicLiveData<T> (private val interval: Long, initialValue: T?) {
    private val mutex = Mutex(false) // private mutable state flow
    private val _liveData = MutableLiveData<T?>(null)
    val asLiveData : LiveData<T?>
        get() = _liveData

    init {
        if (initialValue != null) tryUpdate(initialValue)
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

    private suspend fun apply (next: T) {
        _liveData.postValue(next)
        delay(interval)
    }

    suspend fun update(nextValue: T) {
        mutex.tryLock()
        apply(nextValue)
        mutex.unlock()
    }
}