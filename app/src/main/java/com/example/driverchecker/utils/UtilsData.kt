package com.example.driverchecker.utils

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.room.ColumnInfo
import kotlinx.coroutines.*


sealed interface IPage

sealed class Page : IPage {
    object Camera : Page()
    object Result : Page()
}

/* DATA */
interface Data<T> {
    val value: T
}

interface ObservableData<T> : Data<T> {
    val liveData: LiveData<T>

    fun observe(owner: LifecycleOwner, block: (T) -> Unit): Observer<T>
    fun observeForever(block: (T) -> Unit): Observer<T>
}

interface MutableData<T> : Data<T> {
    fun tryApply (next: T) : Boolean
    suspend fun apply (next: T)
}

interface MutableObservableData<T> : MutableData<T>, ObservableData<T> {
    fun postValue(next: T)
}

interface AtomicData<T> : MutableData<T> {
    fun tryUpdate (next: T) : Boolean
    suspend fun update (next: T)
    fun isLocked(): Boolean
}

interface AtomicObservableData<T> : AtomicData<T>, MutableObservableData<T>

interface CompletableData<T> : ObservableData<T> {
    val promise: Deferred<T>

    suspend fun await ()

    fun tryAwait () : Boolean

    fun deferredAwait ()

    fun isCompleted () : Boolean

    fun cancel (cause: CancellationException)

    fun complete (next: T) : Boolean
}

interface MutableCompletableData<T> : CompletableData<T> {
    fun reset ()
}