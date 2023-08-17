package com.example.driverchecker.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.*

open class MutableStateLiveData<T> (initialValue: T?) {
    var lastValue: T? = null
        protected set
    protected val mLiveData = MutableLiveData<T?>(null)
    val asLiveData: LiveData<T?>
        get() = mLiveData

    init {
        if (initialValue != null) {
            mLiveData.postValue(initialValue)
            lastValue = initialValue
        }
    }

    fun postValue(next: T?) = runBlocking { apply(next) }

    protected open suspend fun apply(next: T?) {
        mLiveData.postValue(next)
        lastValue = next
    }
}