package com.example.driverchecker.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

open class AtomicLiveData<T> (initialValue: T?) : AtomicValue<T> (null) {
    protected val _liveData = MutableLiveData<T?>(null)
    val asLiveData : LiveData<T?>
        get() = _liveData

    init {
        if (initialValue != null) tryUpdate(initialValue)
    }

    override suspend fun apply (next: T) {
        _liveData.postValue(next)
    }
}