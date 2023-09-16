package com.example.driverchecker.utils

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import kotlinx.coroutines.runBlocking

open class StatefulData<T> (initialData: T) : UpdatableData<T> (initialData), MutableObservableData<T> {
    protected val mLiveData = MutableLiveData(initialData)
    override val liveData: LiveData<T>
        get() = mLiveData

    override fun tryApply(next: T) : Boolean {
        value = next
        mLiveData.value = next

        return true
    }

    override suspend fun apply(next: T)  {
        mLiveData.postValue(next)
        value = next
    }

    override fun observe(owner: LifecycleOwner, block: (T) -> Unit): Observer<T> =
        Observer<T> { block(it) }
            .also { observer -> liveData.observe(owner, observer) }
            .also { _ -> mLiveData.postValue(value) }

    override fun observeForever(block: (T) -> Unit): Observer<T> =
        Observer<T> { block(it) }
            .also { observer -> liveData.observeForever(observer) }
            .also { _ -> mLiveData.postValue(value) }

    override fun postValue(next: T) = runBlocking { apply(next) }
}