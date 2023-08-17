package com.example.driverchecker.utils

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

open class StateLiveData<T> (initialValue: T?) {
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

    /**
     * @return Observer instance that has been created, useful if you later want to remove the observer
     */
    fun observe(owner: LifecycleOwner, block: (T?) -> Unit): Observer<T?> =
        Observer<T?> { block(it) }
            .also { observer -> asLiveData.observe(owner, observer) }
            .also { _ -> mLiveData.postValue(lastValue) }

    /**
     * @return Observer instance that has been created, useful if you later want to remove the observer
     */
    fun observeForever(block: (T?) -> Unit): Observer<T?> =
        Observer<T?> { block(it) }
            .also { observer -> asLiveData.observeForever(observer) }
            .also { _ -> mLiveData.postValue(lastValue) }
}