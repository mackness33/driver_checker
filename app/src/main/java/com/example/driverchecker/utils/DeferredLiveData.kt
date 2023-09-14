package com.example.driverchecker.utils

import androidx.lifecycle.LiveData
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

open class DeferredLiveData<T> (initialValue: T?, protected val cxt: CoroutineContext){
    protected var promise = CompletableDeferred<T?>()
    protected val mLiveData: MutableStateLiveData<T?> = StatefulLiveData(null)
    val value: StateLiveData<T?>
        get() = mLiveData
    val asLiveData: LiveData<T?>
        get() = mLiveData.asLiveData

    init {
        if (initialValue != null) {
            mLiveData.postValue(initialValue)
        }
    }

    open suspend fun apply(next: T?) {
        mLiveData.postValue(next)
    }


    suspend fun await () = apply(promise.await())

    fun deferredAwait () {
        runBlocking (cxt) {
            launch (cxt) {
                await()
            }
        }
    }

    fun tryAwait () {
        if (promise.isCompleted) {
            mLiveData.postValue(value.lastValue)
        }
    }

    fun isCompleted () : Boolean = promise.isCompleted

    fun cancel (cause: CancellationException) = promise.cancel(cause)

    fun complete (value: T?) = promise.complete(value)

    fun reset () {
        runBlocking {
            promise.cancelAndJoin()
            promise = CompletableDeferred()
            mLiveData.postValue(null)
        }
    }
}