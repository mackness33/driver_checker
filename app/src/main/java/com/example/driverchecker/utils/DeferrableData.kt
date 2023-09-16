package com.example.driverchecker.utils

import androidx.lifecycle.LiveData
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

open class DeferrableData<T> (protected val initialValue: T, protected val cxt: CoroutineContext) : MutableCompletableData<T>, StatefulData<T> (initialValue) {
    protected var mPromise: CompletableDeferred<T> = CompletableDeferred()
    override val promise: Deferred<T>
        get() = mPromise

    override suspend fun await () = apply(promise.await())

    override fun deferredAwait () {
        runBlocking (cxt) {
            launch (cxt) {
                await()
            }
        }
    }

    override fun tryAwait () : Boolean {
        if (!promise.isCompleted) {
            return tryApply(value)
        }

        return false
    }

    override fun isCompleted () : Boolean = promise.isCompleted

    override fun cancel (cause: CancellationException) = promise.cancel(cause)

    override fun complete (next: T) : Boolean = mPromise.complete(next)

    override fun reset () {
        runBlocking {
            promise.cancelAndJoin()
            mPromise = CompletableDeferred()
            mLiveData.postValue(initialValue)
        }
    }
}