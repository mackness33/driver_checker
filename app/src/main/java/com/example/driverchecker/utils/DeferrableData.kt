package com.example.driverchecker.utils

import androidx.lifecycle.LiveData
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

open class DeferrableData<T> (initialValue: T, protected val cxt: CoroutineContext) : MutableCompletableData<T>, StatefulData<T> (initialValue) {
    protected var promise = CompletableDeferred<T>()

    override suspend fun await () = apply(promise.await())

    override fun deferredAwait () {
        runBlocking (cxt) {
            launch (cxt) {
                await()
            }
        }
    }

    fun tryAwait () {
        if (promise.isCompleted) {
            tryApply(value)
        }
    }

    override fun isCompleted () : Boolean = promise.isCompleted

    override fun cancel (cause: CancellationException) = promise.cancel(cause)

    override fun complete (next: T) : Boolean = promise.complete(next)

    override fun reset () {
        runBlocking {
            promise.cancelAndJoin()
            promise = CompletableDeferred()
            mLiveData.postValue(null)
        }
    }
}