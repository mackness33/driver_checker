package com.example.driverchecker.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

open class DeferredLiveData<T> (initialValue: T?, val cxt: CoroutineContext){
    protected var promise = CompletableDeferred<T?>()
    protected val mLiveData = StateLiveData<T?>(null)
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

    @OptIn(ExperimentalCoroutinesApi::class)
    fun tryAwait () {
        if (promise.isCompleted) {
            mLiveData.postValue(promise.getCompleted())
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