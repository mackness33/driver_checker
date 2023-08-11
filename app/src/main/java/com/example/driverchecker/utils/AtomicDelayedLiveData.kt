package com.example.driverchecker.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex

open class AtomicDelayedLiveData<T> (private val interval: Long, initialValue: T?) : AtomicLiveData<T> (initialValue){
    override suspend fun apply (next: T) {
        super.apply(next)
        delay(interval)
    }
}