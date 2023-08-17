package com.example.driverchecker.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

open class AtomicLiveData<T> (initialValue: T?) : AtomicValue<T> (null) {
    protected val mLiveData = MutableLiveData<T?>(null)
    val asLiveData : LiveData<T?>
        get() = mLiveData

    init {
        if (initialValue != null) tryUpdate(initialValue)
    }

    override suspend fun apply (next: T) {
        mLiveData.postValue(next)
        value = next
    }
}