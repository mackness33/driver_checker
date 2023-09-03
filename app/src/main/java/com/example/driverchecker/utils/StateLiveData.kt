package com.example.driverchecker.utils

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

interface StateLiveData<T> {
    val lastValue: T?
    val asLiveData: LiveData<T?>

    fun observe(owner: LifecycleOwner, block: (T?) -> Unit): Observer<T?>
    fun observeForever(block: (T?) -> Unit): Observer<T?>
}

interface MutableStateLiveData<T> : StateLiveData<T> {
    fun postValue(next: T?)
}